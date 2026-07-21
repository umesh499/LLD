package designPatterns.structural;

public class Facade {
    public static void main(String[] args) {
         System.out.println(getOtpService());

         //new client
         AutheticationFacade facade = new AutheticationFacade();
         System.out.println(facade.getOtpService());
    }

    private static String getOtpService() {
        //old client
         PasswordValidator passwordValidator = new PasswordValidator();
         TokenService tokenService = new TokenService();
         OTPService otpService = new OTPService();
         if(passwordValidator.validatePassword("iurwieu")){
            otpService.getOTP("iyweriu");
            return tokenService.getToken("skdjfhjksd");
         }
         return null;
    }
   
    
}

class PasswordValidator{
    boolean validatePassword(String password){
        System.out.println("validating password 100");
        return true;
    }
}

class TokenService{
    String getToken(String input){
        System.out.println("fetching token 100");
        return "XYZ";
    }
}

class OTPService{
    String getOTP(String userId){
        return "XYZ"+userId;
    }
}

class AutheticationFacade{
    PasswordValidator passwordValidator;
    TokenService tokenService;
    OTPService otpService;
    AutheticationFacade(){
        passwordValidator = new PasswordValidator();
        tokenService = new TokenService();
        otpService = new OTPService();
    }
     String getOtpService() {
         if(passwordValidator.validatePassword("iurwieu")){
            otpService.getOTP("iyweriu");
            return tokenService.getToken("skdjfhjksd");
         }
         return null;
    }

}
