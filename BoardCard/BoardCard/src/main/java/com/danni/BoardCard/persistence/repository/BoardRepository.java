package com.danni.BoardCard.persistence.repository;

import com.danni.BoardCard.persistence.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
}
