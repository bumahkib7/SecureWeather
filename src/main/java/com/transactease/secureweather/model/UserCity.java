package com.transactease.secureweather.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("user_cities")
public class UserCity {

    @Id
    private UUID id;
    private UUID userId;
    private UUID cityId;

    public UserCity() {
    }


    public UUID getId() {
        return this.id;
    }

    public UUID getUserId(UUID userId) {
        return this.userId;
    }

    public UUID getCityId() {
        return this.cityId;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setCityId(UUID cityId) {
        this.cityId = cityId;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof UserCity)) return false;
        final UserCity other = (UserCity) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$userId = this.getUserId(userId);
        final Object other$userId = other.getUserId(userId);
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final Object this$cityId = this.getCityId();
        final Object other$cityId = other.getCityId();
        if (this$cityId == null ? other$cityId != null : !this$cityId.equals(other$cityId)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UserCity;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $userId = this.getUserId(userId);
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final Object $cityId = this.getCityId();
        result = result * PRIME + ($cityId == null ? 43 : $cityId.hashCode());
        return result;
    }

    public String toString() {
        return "UserCity(id=" + this.getId() + ", userId=" + this.getUserId(userId) + ", cityId=" + this.getCityId() + ")";
    }
}
