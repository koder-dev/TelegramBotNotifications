CREATE TABLE IF NOT EXISTS app_user (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        telegram_user_id BIGINT,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        first_name VARCHAR(255),
    last_name VARCHAR(255),
    username VARCHAR(255),
    email VARCHAR(255),
    is_active BOOLEAN,
    user_state VARCHAR(255),
    editing_notification BIGINT
    );

CREATE TABLE IF NOT EXISTS binary_content (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              file LONGBLOB
);

CREATE TABLE IF NOT EXISTS app_audio (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         telegram_file_id VARCHAR(255),
    file_name VARCHAR(255),
    download_link VARCHAR(255),
    user_id BIGINT,
    binary_content_id BIGINT,
    mime_type VARCHAR(255),
    CONSTRAINT fk_audio_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_audio_binary FOREIGN KEY (binary_content_id) REFERENCES binary_content(id)
    );

CREATE TABLE IF NOT EXISTS app_doc (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            telegram_file_id VARCHAR(255),
    file_name VARCHAR(255),
    download_link VARCHAR(255),
    user_id BIGINT,
    binary_content_id BIGINT,
    mime_type VARCHAR(255),
    CONSTRAINT fk_document_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_document_binary FOREIGN KEY (binary_content_id) REFERENCES binary_content(id)
    );

CREATE TABLE IF NOT EXISTS app_notification (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                user_id BIGINT,
                                                notify_time TIMESTAMP,
                                                notify_text VARCHAR(255),
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS app_photo (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         telegram_file_id VARCHAR(255),
    file_name VARCHAR(255),
    download_link VARCHAR(255),
    user_id BIGINT,
    binary_content_id BIGINT,
    mime_type VARCHAR(255),
    CONSTRAINT fk_photo_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_photo_binary FOREIGN KEY (binary_content_id) REFERENCES binary_content(id)
    );

CREATE TABLE IF NOT EXISTS app_video (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         telegram_file_id VARCHAR(255),
    file_name VARCHAR(255),
    download_link VARCHAR(255),
    user_id BIGINT,
    binary_content_id BIGINT,
    mime_type VARCHAR(255),
    CONSTRAINT fk_video_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_video_binary FOREIGN KEY (binary_content_id) REFERENCES binary_content(id)
    );
