package com.danni.BoardCard.dto;

import com.danni.BoardCard.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind) {
}
