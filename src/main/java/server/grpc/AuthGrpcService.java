package server.grpc;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.AuthGrpcServiceGrpc.AuthGrpcServiceImplBase;
import server.AuthProto.LoginRequest;
import server.AuthProto.LoginResponse;
import server.services.implementations.AuthService;

@GrpcService
public class AuthGrpcService extends AuthGrpcServiceImplBase {

	private final AuthService authService;

	public AuthGrpcService(AuthService authService) {
		this.authService = authService;
	}

	@Override
	public void loginGrpc(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {

		String username = request.getUsername();
		String password = request.getPassword();
		
		try {
			
			var userAuth = authService.login(username, password);
			
			String token = userAuth.getToken();
			
			List<String> roles = 
					userAuth.getRoles()
					.stream()
					.map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

			LoginResponse response = 
					LoginResponse
					.newBuilder()
					.setToken(token)
					.setUsername(userAuth.getUserName())
					.addAllRoles(roles)
					.build();

			responseObserver.onNext(response);
			responseObserver.onCompleted();
			
		}
		
		catch (RuntimeException e) {
			
			responseObserver
			.onError(Status.UNAUTHENTICATED.withDescription(e.getMessage())
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
