//package ca.uqtr.authservice.exception;
//
//@Component
//public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
//
//    @Autowired
//    private MessageSource messages;
//
//    @Autowired
//    private LocaleResolver localeResolver;
//
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request,
//                                        HttpServletResponse response, AuthenticationException exception)
//            throws IOException, ServletException {
//        setDefaultFailureUrl("/login.html?error=true");
//
//        super.onAuthenticationFailure(request, response, exception);
//
//        Locale locale = localeResolver.resolveLocale(request);
//
//        String errorMessage = messages.getMessage("message.badCredentials", null, locale);
//
//        if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
//            errorMessage = messages.getMessage("auth.message.disabled", null, locale);
//        } else if (exception.getMessage().equalsIgnoreCase("User account has expired")) {
//            errorMessage = messages.getMessage("auth.message.expired", null, locale);
//        }
//
//        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
//    }
//}