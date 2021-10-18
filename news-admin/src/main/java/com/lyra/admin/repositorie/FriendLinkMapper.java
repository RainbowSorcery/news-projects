package com.lyra.admin.repositorie;

import com.lyra.pojo.mo.FriendLinkMO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FriendLinkMapper extends MongoRepository<FriendLinkMO, String> {

    List<FriendLinkMO> findAllByIsDelete(Integer isDelete);
}
