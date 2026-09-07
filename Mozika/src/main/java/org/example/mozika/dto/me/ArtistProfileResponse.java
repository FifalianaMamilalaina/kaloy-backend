package org.example.mozika.dto.me;

import java.time.LocalDateTime;
import java.util.List;

public class ArtistProfileResponse {
    private Long userId;
    private String email;
    private String emailVerificationStatus;
    private String phone;
    private String phoneVerificationStatus;
    private String accountStatus;
    private LocalDateTime memberSince;
    private String artistType;
    private String stageName;
    private Integer activeSinceYear;
    private String photoUrl;
    private String bio;
    private String verificationStatus;
    private Boolean isCertified;
    // Populated only for GROUP type
    private List<GroupMemberResponse> members;
    private List<GroupMemberResponse> activeMembers;

    public ArtistProfileResponse() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEmailVerificationStatus() { return emailVerificationStatus; }
    public void setEmailVerificationStatus(String v) { this.emailVerificationStatus = v; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhoneVerificationStatus() { return phoneVerificationStatus; }
    public void setPhoneVerificationStatus(String v) { this.phoneVerificationStatus = v; }
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
    public LocalDateTime getMemberSince() { return memberSince; }
    public void setMemberSince(LocalDateTime memberSince) { this.memberSince = memberSince; }
    public String getArtistType() { return artistType; }
    public void setArtistType(String artistType) { this.artistType = artistType; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public Integer getActiveSinceYear() { return activeSinceYear; }
    public void setActiveSinceYear(Integer activeSinceYear) { this.activeSinceYear = activeSinceYear; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public Boolean getIsCertified() { return isCertified; }
    public void setIsCertified(Boolean isCertified) { this.isCertified = isCertified; }
    public List<GroupMemberResponse> getMembers() { return members; }
    public void setMembers(List<GroupMemberResponse> members) { this.members = members; }
    public List<GroupMemberResponse> getActiveMembers() { return activeMembers; }
    public void setActiveMembers(List<GroupMemberResponse> activeMembers) { this.activeMembers = activeMembers; }
}
