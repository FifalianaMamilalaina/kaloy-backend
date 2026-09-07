package org.example.mozika.dto.me;

import java.time.LocalDateTime;

public class ClientProfileResponse {
    private Long userId;
    private String email;
    private String emailVerificationStatus;
    private String phone;
    private String phoneVerificationStatus;
    private String accountStatus;
    private LocalDateTime memberSince;
    private String firstName;
    private String lastName;
    private String userName;
    private String photoUrl;

    public ClientProfileResponse() {}

    public ClientProfileResponse(Long userId, String email, String emailVerificationStatus,
                                 String phone, String phoneVerificationStatus, String accountStatus,
                                 LocalDateTime memberSince, String firstName, String lastName,
                                 String userName, String photoUrl) {
        this.userId = userId;
        this.email = email;
        this.emailVerificationStatus = emailVerificationStatus;
        this.phone = phone;
        this.phoneVerificationStatus = phoneVerificationStatus;
        this.accountStatus = accountStatus;
        this.memberSince = memberSince;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.photoUrl = photoUrl;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEmailVerificationStatus() { return emailVerificationStatus; }
    public void setEmailVerificationStatus(String emailVerificationStatus) { this.emailVerificationStatus = emailVerificationStatus; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhoneVerificationStatus() { return phoneVerificationStatus; }
    public void setPhoneVerificationStatus(String phoneVerificationStatus) { this.phoneVerificationStatus = phoneVerificationStatus; }
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
    public LocalDateTime getMemberSince() { return memberSince; }
    public void setMemberSince(LocalDateTime memberSince) { this.memberSince = memberSince; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
