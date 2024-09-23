package server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import server.security.GrpcSecurityConfig.RoleAuth;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.security.core.Authentication;

public class AuthInterceptor implements ServerInterceptor {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtProvider jwtProvider;
	
	private Method getGrpcMethod(ServerCall<?, ?> call) {
		
	    try {
	    	
	        String fullMethodName = call.getMethodDescriptor().getFullMethodName();
	        String serviceName = fullMethodName.substring(0, fullMethodName.lastIndexOf('/'));
	        String methodName = fullMethodName.substring(fullMethodName.lastIndexOf('/') + 1);

	        String simpleServiceName = serviceName.substring(serviceName.lastIndexOf('.') + 1);
	        
	        // normalization
	        String packageServiceName = "server.grpc." + simpleServiceName;     	
	
	        Class<?> serviceClass = Class.forName(packageServiceName);
	        
	        for (Method method : serviceClass.getMethods()) {
	        	
	            if (method.getName().equalsIgnoreCase(methodName)) {
	                return method;
	            }
	        }	        
	    } 
	    
	    catch (Exception e) {	    
	    	
	        e.printStackTrace();
	    }
	    
	    return null;
	}
    
	private boolean hasRequiredRole(Authentication authentication, String[] requiredRoles) {
		
	    return Arrays.stream(requiredRoles)
	        .anyMatch(role -> authentication.getAuthorities().stream()
	        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role)));
	}

	@Override
	public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
			ServerCallHandler<ReqT, RespT> next) {
		
		String publicRouter = "auth.AuthGrpcService/LoginGrpc";

		if (call.getMethodDescriptor().getFullMethodName().equals(publicRouter)) {
			return next.startCall(call, headers);
		}

		String authHeader = headers.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			
			call.close(Status.UNAUTHENTICATED.withDescription("Authentication required"), headers);
			return new ServerCall.Listener<>() {};
		}

		String token = authHeader.substring("Bearer ".length()).trim();

		if (!jwtProvider.validateToken(token)) {

			call.close(Status.UNAUTHENTICATED.withDescription("Invalid or expired token"), headers);

			return new ServerCall.Listener<>() {};
		}

		String username = jwtProvider.getUsernameFromToken(token);

		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		if (userDetails == null) {
			
			call.close(Status.UNAUTHENTICATED.withDescription("User not found"), headers);
			
			return new ServerCall.Listener<>() {};
		}

		UsernamePasswordAuthenticationToken authentication = 
		new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		Method method = getGrpcMethod(call);
		 
        if (method != null && method.isAnnotationPresent(RoleAuth.class)) {
        	
            RoleAuth roleAuth = method.getAnnotation(RoleAuth.class);
            
            if (!hasRequiredRole(authentication, roleAuth.value())) {
            	
                call.close(Status.PERMISSION_DENIED.withDescription("You don't have permissions"), headers);
                
                return new ServerCall.Listener<>() {};
            }
        }

		return Contexts.interceptCall(Context.current(), call, headers, next);
	}
}
