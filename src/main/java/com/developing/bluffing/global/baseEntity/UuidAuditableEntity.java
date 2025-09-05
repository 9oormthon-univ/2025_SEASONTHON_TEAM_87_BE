package com.developing.bluffing.global.baseEntity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;


// Id -> PK 를 UUID로 가지는 엔티티의 부모 클래스
// 생성,수정,삭제를 감시하는 Auditable 상속자
@Getter
@MappedSuperclass
public abstract class UuidAuditableEntity extends AuditableEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    protected UUID id;

    @PrePersist
    protected void generateUuid() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch(); // UUIDv7 생성
        }
    }

}
