INSERT INTO genres (name) VALUES ('Folk progressif') RETURNING id;

INSERT INTO song_genres (song_id, genre_id) VALUES 
    (2, 1),  
    (3, 1),  
    (4, 1),  
    (5, 1);  