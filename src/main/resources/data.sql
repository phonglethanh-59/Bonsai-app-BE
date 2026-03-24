-- Categories
INSERT IGNORE INTO categories (id, name, description) VALUES
(1, 'Bonsai trong nhà', 'Các loại bonsai phù hợp để trưng bày trong nhà, văn phòng'),
(2, 'Bonsai ngoài trời', 'Các loại bonsai thích hợp trồng ngoài trời, sân vườn'),
(3, 'Bonsai mini', 'Bonsai kích thước nhỏ, phù hợp để bàn làm việc'),
(4, 'Bonsai phong thủy', 'Bonsai mang ý nghĩa phong thủy, tài lộc');

-- Products (10 sản phẩm bonsai)
INSERT IGNORE INTO products (id, sku, name, description, price, origin, supplier, cover_image_url, age, height, pot_type, care_level, stock_quantity, featured, category_id, average_rating, review_count) VALUES
(1, 'BON-001', 'Bonsai Tùng La Hán',
 'Cây Tùng La Hán bonsai dáng trực, thân xù xì cổ kính. Lá xanh quanh năm, biểu tượng của sự trường thọ và may mắn. Rất phù hợp làm quà tặng hoặc trưng bày phòng khách.',
 2500000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1599598177991-ec67b5c37318?w=600',
 5, 40, 'Chậu sứ tráng men', 'MEDIUM', 15, true, 1, 4.5, 12),

(2, 'BON-002', 'Bonsai Sanh Cổ Thụ',
 'Cây Sanh bonsai dáng cổ thụ, rễ bám đá tự nhiên. Thân cây uốn lượn mềm mại, tán lá xanh tươi tốt. Thích hợp trưng bày sân vườn hoặc ban công.',
 3800000, 'Việt Nam', 'Vườn Bonsai Hà Nội',
 'https://images.unsplash.com/photo-1567331711402-509c12c41959?w=600',
 8, 55, 'Chậu đá mài', 'MEDIUM', 8, true, 2, 4.8, 20),

(3, 'BON-003', 'Bonsai Mai Chiếu Thủy',
 'Cây Mai Chiếu Thủy bonsai mini, hoa trắng nhỏ thơm nhẹ. Dáng cây thanh thoát, phù hợp đặt bàn làm việc hoặc bàn trà. Dễ chăm sóc, ít cần tưới nước.',
 450000, 'Việt Nam', 'Vườn Bonsai Đà Lạt',
 'https://images.unsplash.com/photo-1509423350716-97f9360b4e09?w=600',
 3, 20, 'Chậu gốm nhỏ', 'EASY', 30, true, 3, 4.3, 35),

(4, 'BON-004', 'Bonsai Kim Tiền',
 'Cây Kim Tiền (Zamia) bonsai phong thủy, lá xanh bóng mượt. Tượng trưng cho tài lộc, thịnh vượng. Cực kỳ dễ chăm sóc, chịu bóng tốt, phù hợp văn phòng.',
 650000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1593482892540-56b4b2a3a9b6?w=600',
 2, 35, 'Chậu sứ trắng', 'EASY', 25, true, 4, 4.6, 18),

(5, 'BON-005', 'Bonsai Phi Lao',
 'Cây Phi Lao bonsai dáng hoành, thân cây mạnh mẽ với vỏ nứt nẻ tự nhiên. Lá kim xanh mướt, rất đẹp khi đặt ngoài trời. Cần chăm sóc cẩn thận về nước và ánh sáng.',
 5200000, 'Nhật Bản', 'Bonsai Japan Import',
 'https://images.unsplash.com/photo-1512428813834-c702c7702b78?w=600',
 12, 60, 'Chậu Nhật Bản', 'HARD', 5, true, 2, 4.9, 8),

(6, 'BON-006', 'Bonsai Đa Búp Đỏ Mini',
 'Cây Đa Búp Đỏ bonsai mini, búp non màu đỏ rực rỡ. Rễ khí phát triển tự nhiên tạo vẻ đẹp hoang sơ. Kích thước nhỏ gọn, phù hợp để bàn.',
 350000, 'Việt Nam', 'Vườn Bonsai Huế',
 'https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=600',
 2, 18, 'Chậu gốm mini', 'EASY', 40, false, 3, 4.2, 25),

(7, 'BON-007', 'Bonsai Lộc Vừng',
 'Cây Lộc Vừng bonsai phong thủy, hoa đỏ rủ dài tuyệt đẹp. Mang ý nghĩa lộc - tài - phúc. Thân cây to khỏe, dáng cổ thụ uy nghi. Trưng bày trước nhà hoặc sân vườn.',
 8500000, 'Việt Nam', 'Vườn Bonsai Hà Nội',
 'https://images.unsplash.com/photo-1604762524889-3e2fcc145683?w=600',
 15, 70, 'Chậu xi măng giả đá', 'MEDIUM', 3, true, 4, 4.7, 10),

(8, 'BON-008', 'Bonsai Linh Sam',
 'Cây Linh Sam bonsai dáng bay, cành lá phát triển một phía tạo cảm giác cây bị gió thổi. Lá nhỏ xanh đậm, thân cây uốn lượn nghệ thuật.',
 1800000, 'Việt Nam', 'Vườn Bonsai Đà Nẵng',
 'https://images.unsplash.com/photo-1620803366004-119b57f54cd6?w=600',
 6, 45, 'Chậu sứ xanh', 'MEDIUM', 10, false, 1, 4.4, 15),

(9, 'BON-009', 'Bonsai Thông Đen Nhật',
 'Cây Thông Đen Nhật Bản (Pinus thunbergii) bonsai cao cấp. Dáng trực hùng vĩ, lá kim cứng cáp. Biểu tượng của sức mạnh và sự kiên cường trong văn hóa Nhật.',
 12000000, 'Nhật Bản', 'Bonsai Japan Import',
 'https://images.unsplash.com/photo-1610652492500-ded49ceeb378?w=600',
 20, 50, 'Chậu Tokoname Nhật', 'HARD', 2, true, 2, 5.0, 5),

(10, 'BON-010', 'Bonsai Cần Thăng Mini',
 'Cây Cần Thăng bonsai mini, lá nhỏ li ti xanh mướt. Dáng cây tự nhiên, dễ uốn tạo hình. Phù hợp cho người mới bắt đầu chơi bonsai.',
 280000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1595351298020-038700609878?w=600',
 1, 15, 'Chậu nhựa cao cấp', 'EASY', 50, false, 3, 4.1, 30);
