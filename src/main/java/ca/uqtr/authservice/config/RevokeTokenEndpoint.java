//package ca.uqtr.authservice.config;
//
//import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
//import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
//@FrameworkEndpoint
//@RestController
//public class RevokeTokenEndpoint {
//
//    @Resource(name = "tokenServices")
//    private ConsumerTokenServices tokenServices;
//
//    @DeleteMapping("/oauth/token")
//    public void revokeToken(HttpServletRequest request) {
//        String authorization = request.getHeader("Authorization");
//        if (authorization != null && authorization.contains("Bearer")){
//            String tokenId = authorization.substring("Bearer".length()+1);
//            tokenServices.revokeToken(tokenId);
//        }
//    }
//}
