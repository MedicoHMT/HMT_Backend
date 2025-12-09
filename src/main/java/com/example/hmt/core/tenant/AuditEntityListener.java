package com.example.hmt.core.tenant;

import com.example.hmt.core.auth.model.User;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.util.UUID;

public class AuditEntityListener {
    @PrePersist
    protected void prePersist(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            Long hId = TenantContext.getHospitalId();

            if (baseEntity.getHospital() == null) {
                if (hId == null)
                    throw new IllegalStateException("Hospital context missing for insert operation");
                Hospital hospital = new Hospital();
                hospital.setId(hId);
                baseEntity.setHospital(hospital);
            }

            UUID uId = TenantContext.getUserId();
            if (uId == null) {
                throw new IllegalStateException("User context missing for insert operation");
            }

            if (baseEntity.getCreatedBy() == null) {
                User user = new User();
                user.setId(uId);
                baseEntity.setCreatedBy(user);
            }

            User user = new User();
            user.setId(uId);
            baseEntity.setUpdatedBy(user);
        }
    }

    @PreUpdate
    protected void preUpdate(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            UUID uId = TenantContext.getUserId();
            if (uId == null)
                throw new IllegalStateException("User context missing for insert operation");
            User user = new User();
            user.setId(uId);
            baseEntity.setUpdatedBy(user);
        }
    }
}
