package com.furnaghan.spring.jsonrpc.server.rpc;

import static java.lang.String.format;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furnaghan.spring.jsonrpc.api.RpcMethod;
import com.furnaghan.spring.jsonrpc.server.error.InvalidParameterException;
import com.furnaghan.spring.jsonrpc.server.error.ParseException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;

public class ApiMethod {

	private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
	private static final ExecutableValidator EXECUTABLE_VALIDATOR = FACTORY.getValidator()
			.forExecutables();

	private final Method method;
	private final ObjectMapper objectMapper;
	private final Parameter[] parameters;
	private final String name;

	public ApiMethod( final Method method, final ObjectMapper objectMapper ) {
		this.method = method;
		this.objectMapper = objectMapper;
		this.parameters = method.getParameters();
		this.name = Optional.ofNullable( method.getAnnotation( RpcMethod.class ) )
				.map( RpcMethod::name )
				.filter( n -> !n.isEmpty() )
				.orElseGet( method::getName );

	}

	public String getName() {
		return name;
	}

	public Object invoke( final Object object, final Object params )
			throws InvocationTargetException, IllegalAccessException {
		final Object[] methodParams = toMethodParams( params );

		if ( methodParams.length != parameters.length ) {
			throw new InvalidParameterException( parameters );
		}

		for ( int i = 0; i < parameters.length; i++ ) {
			final Class<?> paramType = parameters[i].getType();
			final Object param = methodParams[i];
			if ( !paramType.isInstance( param ) ) {
				throw new InvalidParameterException( parameters );
			}
		}

		validateParameters( object, method, methodParams );
		return method.invoke( object, methodParams );
	}

	private Object[] toMethodParams( final Object params ) {
		if ( params instanceof List<?> list ) {
			return IntStream.range( 0, parameters.length ).mapToObj( index -> {
				final Parameter parameter = parameters[index];
				final Object value = index < list.size() ? list.get( index ) : null;
				return objectMapper.convertValue( value, parameter.getType() );
			} ).toArray();
		}

		if ( params instanceof Map<?, ?> map ) {
			return Stream.of( parameters ).map( parameter -> {
				final Object value = map.get( parameter.getName() );
				return objectMapper.convertValue( value, parameter.getType() );
			} ).toArray();
		}

		throw new ParseException( "Unknown param type: " + params.getClass().getSimpleName() );
	}

	private static void validateParameters( final Object object, final Method method,
			final Object[] params ) {
		final Set<ConstraintViolation<Object>> violations = EXECUTABLE_VALIDATOR.validateParameters(
				object, method, params );

		if ( violations.isEmpty() ) {
			return;
		}

		final Map<String, String> violationMessagesByParameterName = violations.stream()
				.collect( groupingBy( violation -> {
					final Path path = violation.getPropertyPath();
					final String string = path.toString();
					final int indexOfDot = string.indexOf( "." );
					if ( indexOfDot > 0 ) {
						return string.substring( indexOfDot + 1 );
					}
					return string;
				}, collectingAndThen( toList(), results -> results.stream()
						.map( ConstraintViolation::getMessage )
						.sorted()
						.collect( joining( " AND " ) ) ) ) );

		throw new IllegalArgumentException( violationMessagesByParameterName.entrySet()
				.stream()
				.map( entry -> format( "Parameter %s %s", entry.getKey(), entry.getValue() ) )
				.collect( joining( ", " ) ) );
	}
}
