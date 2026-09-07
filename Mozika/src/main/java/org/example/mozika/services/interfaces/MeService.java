package org.example.mozika.services.interfaces;

import org.example.mozika.dto.me.*;

import java.util.List;

public interface MeService {

    Object getMyProfile(String email);

    void updatePersonalInfo(String email, UpdatePersonalInfoRequest request);

    void updateArtistProfile(String email, UpdateArtistProfileRequest request);

    void updatePhoto(String email, UpdatePhotoRequest request);

    void initiateEmailChange(String email, ChangeEmailRequest request);

    void confirmEmailChange(String email, ConfirmEmailRequest request);

    void initiatePhoneChange(String email, ChangePhoneRequest request);

    void confirmPhoneChange(String email, ConfirmPhoneRequest request);

    void requestVerification(String email);

    List<GroupMemberResponse> getGroupMembers(String email);

    GroupMemberResponse addGroupMember(String email, AddGroupMemberRequest request);

    GroupMemberResponse updateGroupMember(String email, Long memberId, UpdateGroupMemberRequest request);

    GroupMemberResponse updateGroupMemberStatus(String email, Long memberId, GroupMemberStatusRequest request);

    void deleteGroupMember(String email, Long memberId);

    List<InstrumentRoleResponse> getInstrumentRoles();

    void deleteMyAccount(String email);
}
