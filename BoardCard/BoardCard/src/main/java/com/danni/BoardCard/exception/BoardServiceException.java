package com.danni.BoardCard.exception;

/**
 * Exceção lançada quando ocorrem erros nas operações do BoardService
 */
public class BoardServiceException extends RuntimeException {

  public BoardServiceException(String message) {
    super(message);
  }

  public BoardServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}