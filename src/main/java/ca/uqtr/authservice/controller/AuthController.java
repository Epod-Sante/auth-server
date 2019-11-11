//package ca.uqtr.authservice.controller;
//
//import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
//@Controller
//@RequestMapping("/")
//public class AuthController {
//    @Resource(name = "tokenServices")
//    private ConsumerTokenServices tokenServices;
//
//    @Resource(name = "jdbcTokenStore")
//    private TokenStore tokenStore;
//
//    @RequestMapping(method = RequestMethod.GET, value = "/logout/{token}")
//    @ResponseBody
//    public void revokeToken(@PathVariable String token) {
//        System.out.println(token);
//        tokenServices.revokeToken(token);
//        ((JdbcTokenStore) tokenStore).removeRefreshToken(token);
//    }
//
//
//
//}
