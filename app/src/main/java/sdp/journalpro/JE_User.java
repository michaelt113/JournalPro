package sdp.journalpro;

class JE_User {

    // vars
    private String userId;

    JE_User(String userId) {
        this.userId = userId;
    }

    UserVerified getVerified() {
        return new UserVerified(userId);    // returns current verified user
    }

    // User Constructor
    private static class UserVerified {

        String verified_detail;
        String verified_date;

        UserVerified(String userId) {
            this.verified_detail = "detail_" + userId;
            this.verified_date = "date_" + userId;
        }
    }
}

