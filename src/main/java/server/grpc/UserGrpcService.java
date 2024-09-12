package server.grpc;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.UserGrpcServiceGrpc.UserGrpcServiceImplBase;
import server.UserProto.UserGrpc;
import server.UserProto.RequestId;
import server.services.IUserService;

@GrpcService
public class UserGrpcService extends UserGrpcServiceImplBase {

	@Autowired
	private IUserService services;

	@Override
	public void getUserGrpc(RequestId request, StreamObserver<UserGrpc> responseObserver) {

		try {

			var response = services.findById(request.getId());

			if (response == null) {
				
				String messageError = "User with id " + request.getId() + " not found";
				
				throw new NoSuchElementException(messageError);
			}

			UserGrpc user = 
					UserGrpc
					.newBuilder()
					.setId(response.getId())
					.setName(response.getName())
					.build();

			responseObserver.onNext(user);
			responseObserver.onCompleted();

		}

		catch (NoSuchElementException e) {			
			
			responseObserver
			.onError(Status.NOT_FOUND.withDescription(e.getMessage())
			.asRuntimeException());
		}

		catch (Exception e) {
			
			String messageError = "Internal server error: " + e.getMessage();
			
			responseObserver
			.onError(Status.INTERNAL.withDescription(messageError)
			.asRuntimeException());
		}
	}
}
