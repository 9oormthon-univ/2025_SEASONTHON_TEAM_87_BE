package com.developing.bluffing.global.baseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * LONG 기반 테이블에서 공통으로 쓰는 생성/수정/삭제일자 베이스 엔티티.
 * (UUID 기반은 기존 UuidAuditableEntity를 계속 사용)
 */
@Getter
@MappedSuperclass
public abstract class LongAuditableEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("PK")
    protected Long id;

}

