-- Insert sample data for development
INSERT INTO users (name, email, created_at) VALUES 
('田中太郎', 'tanaka@example.com', NOW()),
('佐藤花子', 'sato@example.com', NOW()),
('鈴木一郎', 'suzuki@example.com', NOW())
ON CONFLICT (email) DO NOTHING;