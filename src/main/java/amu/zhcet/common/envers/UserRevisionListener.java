package amu.zhcet.common.envers;

import amu.zhcet.auth.Auditor;
import org.hibernate.envers.RevisionListener;

public class UserRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        UserRevisionEntity userRevisionEntity = (UserRevisionEntity) revisionEntity;
        userRevisionEntity.setUsername(Auditor.getLoggedInUsername());
    }

}
