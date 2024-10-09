package server.handlers;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import io.grpc.stub.StreamObserver;
import io.grpc.Status;

public class GrpcExceptionHandler {

	public static <T> void handleException(Exception e, StreamObserver<T> responseObserver) {

		if (e instanceof NoSuchElementException) {

			responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
		}
		
		if (e instanceof IllegalStateException) {
	        responseObserver.onError(Status.FAILED_PRECONDITION.withDescription("Failed to create resource: " + e.getMessage()).asRuntimeException());
	    }
		
	    if (e instanceof InterruptedException) {
	        responseObserver.onError(Status.ABORTED.withDescription("Operation was interrupted: " + e.getMessage()).asRuntimeException());
	    }

	    if (e instanceof ExecutionException) {
	        responseObserver.onError(Status.UNKNOWN.withDescription("Execution error: " + e.getMessage()).asRuntimeException());
	    }
		
		// Add more exceptions here:

		// should be the last exception
		else {

			String messageError = "Internal server error: " + e.getMessage();
			
			responseObserver.onError(Status.INTERNAL.withDescription(messageError).asRuntimeException());
		}
	}
}
