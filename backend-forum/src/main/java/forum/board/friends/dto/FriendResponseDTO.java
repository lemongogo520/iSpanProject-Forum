package forum.board.friends.dto;

import java.time.LocalDate;
import java.util.UUID;

public record FriendResponseDTO(

        UUID userId
        
        ,String account
        
        ,String displayName

        ,String email

        ,LocalDate birthday

) {}
