package bursa.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_video")
public class AppVideo implements AppMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramFileId;
    private String fileName;
    private String downloadLink;
    @OneToOne
    private BinaryContent binaryContent;
    private String mimeType;
}
