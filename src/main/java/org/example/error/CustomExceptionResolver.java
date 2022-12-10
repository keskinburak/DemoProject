package org.example.error;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(@NotNull Throwable ex, @NotNull DataFetchingEnvironment env) {
        if (ex instanceof UserNotFoundException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .extensions(((UserNotFoundException) ex).getExtensions())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else if (ex instanceof WrongUserException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .extensions(((WrongUserException) ex).getExtensions())
                    .location(env.getField().getSourceLocation())
                    .build();
        }else if (ex instanceof UserAlreadyJoinException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .extensions(((UserAlreadyJoinException) ex).getExtensions())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else if (ex instanceof UserDidNotJoinException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .extensions(((UserDidNotJoinException) ex).getExtensions())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else if (ex instanceof UserAlreadyExistsException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }
        else {
            return null;
        }
    }

}
