package kg.founders.core.services;


import kg.founders.core.entity.OldPassword;

import java.util.List;

public interface OldPasswordService {
    OldPassword save(OldPassword oldPassword);

    List<OldPassword> getLast5RowsByAuthId(Long authId);
}