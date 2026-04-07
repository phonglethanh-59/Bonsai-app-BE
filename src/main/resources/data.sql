-- Fix version NULL cho products đã tồn tại
UPDATE products SET version = 0 WHERE version IS NULL;

-- Admin account (username: admin, password: 123456)
INSERT IGNORE INTO users (user_id, username, password, role, status, created_at) VALUES
('U0001', 'admin', '$2a$12$gga6/JBsOQsLvoG5zqaLZuMoUj2MhO4cs73fOetmtp30xV8vvb0tG', 'ADMIN', true, NOW());

INSERT IGNORE INTO user_details (full_name, email, phone, gender, user_id) VALUES
('Administrator', 'admin@bonsaishop.com', '0123456789', 'Nam', 'U0001');

-- Categories
INSERT IGNORE INTO categories (id, name, description) VALUES
(1, 'Bonsai trong nhà', 'Các loại bonsai phù hợp để trưng bày trong nhà, văn phòng'),
(2, 'Bonsai ngoài trời', 'Các loại bonsai thích hợp trồng ngoài trời, sân vườn'),
(3, 'Bonsai mini', 'Bonsai kích thước nhỏ, phù hợp để bàn làm việc'),
(4, 'Bonsai phong thủy', 'Bonsai mang ý nghĩa phong thủy, tài lộc'),
(5, 'Bonsai nghệ thuật', 'Bonsai tạo hình nghệ thuật cao cấp, dành cho người sưu tầm'),
(6, 'Bonsai Nhật Bản', 'Bonsai nhập khẩu và phong cách Nhật Bản chính thống');

-- ========================================
-- Products (45 sản phẩm bonsai)
-- ========================================

INSERT IGNORE INTO products (id, sku, name, description, price, origin, supplier, cover_image_url, age, height, pot_type, care_level, stock_quantity, featured, category_id, average_rating, review_count, care_guide) VALUES

-- === 1-10: Sản phẩm gốc ===
(1, 'BON-001', 'Bonsai Tùng La Hán',
 'Cây Tùng La Hán bonsai dáng trực, thân xù xì cổ kính. Lá xanh quanh năm, biểu tượng của sự trường thọ và may mắn. Thân cây phát triển chậm qua nhiều năm tạo nên vẻ đẹp cổ kính tự nhiên. Tán lá dày, xanh đậm quanh năm không rụng. Rất phù hợp làm quà tặng hoặc trưng bày phòng khách, thể hiện sự sang trọng và đẳng cấp.',
 2500000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1599598177991-ec67b5c37318?w=600',
 5, 40, 'Chậu sứ tráng men', 'MEDIUM', 15, true, 1, 4.5, 12,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI TÙNG LA HÁN\n\n📍 Vị trí: Đặt nơi có ánh sáng gián tiếp, tránh nắng gắt trực tiếp. Phù hợp trong nhà gần cửa sổ.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần vào mùa hè, 1-2 lần/tuần vào mùa đông. Kiểm tra đất khô khoảng 2cm trước khi tưới. Tránh để nước đọng ở đáy chậu.\n\n🌡️ Nhiệt độ: Thích hợp 18-28°C. Chịu được nhiệt độ thấp đến 10°C.\n\n✂️ Cắt tỉa: Tỉa lá vàng và cành khô mỗi 2-3 tháng. Cắt ngọn để cây phát triển ngang, tạo dáng đẹp.\n\n🪴 Thay đất: Thay đất và chậu mỗi 2-3 năm vào đầu mùa xuân.\n\n💊 Phân bón: Bón phân NPK loãng mỗi tháng 1 lần trong mùa sinh trưởng (tháng 3-10).'),

(2, 'BON-002', 'Bonsai Sanh Cổ Thụ',
 'Cây Sanh bonsai dáng cổ thụ, rễ bám đá tự nhiên. Thân cây uốn lượn mềm mại, tán lá xanh tươi tốt. Rễ khí phát triển mạnh tạo nên vẻ uy nghi, trầm mặc. Cây Sanh là một trong tứ quý (Sanh - Sung - Tùng - Bách) được giới chơi cây cảnh ưa chuộng. Thích hợp trưng bày sân vườn hoặc ban công.',
 3800000, 'Việt Nam', 'Vườn Bonsai Hà Nội',
 'https://images.unsplash.com/photo-1567331711402-509c12c41959?w=600',
 8, 55, 'Chậu đá mài', 'MEDIUM', 8, true, 2, 4.8, 20,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI SANH CỔ THỤ\n\n📍 Vị trí: Ngoài trời hoặc ban công có nắng sáng. Cần ít nhất 4-6 giờ ánh sáng/ngày.\n\n💧 Tưới nước: Tưới đẫm mỗi ngày vào mùa hè, 2-3 ngày/lần vào mùa đông. Phun sương lên lá vào buổi sáng.\n\n🌡️ Nhiệt độ: Thích hợp 20-35°C. Chịu nóng tốt nhưng cần che khi nắng quá gắt.\n\n✂️ Cắt tỉa: Tỉa cành thường xuyên mỗi tháng để giữ dáng. Cắt rễ khí dài để tạo hình.\n\n🪴 Thay đất: Thay đất mỗi 1-2 năm. Dùng hỗn hợp đất thịt + cát + mùn.\n\n💊 Phân bón: Bón phân hữu cơ hoai mục mỗi 2 tuần trong mùa sinh trưởng.'),

(3, 'BON-003', 'Bonsai Mai Chiếu Thủy',
 'Cây Mai Chiếu Thủy bonsai mini, hoa trắng nhỏ thơm nhẹ quanh năm. Dáng cây thanh thoát, tán lá nhỏ xinh xắn. Hoa nở li ti trắng muốt, tỏa hương thơm dịu nhẹ rất dễ chịu. Phù hợp đặt bàn làm việc hoặc bàn trà, mang lại cảm giác thư thái, yên bình.',
 450000, 'Việt Nam', 'Vườn Bonsai Đà Lạt',
 'https://images.unsplash.com/photo-1509423350716-97f9360b4e09?w=600',
 3, 20, 'Chậu gốm nhỏ', 'EASY', 30, true, 3, 4.3, 35,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI MAI CHIẾU THỦY\n\n📍 Vị trí: Trong nhà gần cửa sổ có ánh sáng nhẹ. Tránh nắng trực tiếp.\n\n💧 Tưới nước: Tưới 2 lần/tuần. Giữ đất ẩm vừa phải, không để úng nước.\n\n🌡️ Nhiệt độ: 20-30°C. Không chịu được lạnh dưới 15°C.\n\n✂️ Cắt tỉa: Tỉa nhẹ sau mỗi đợt hoa tàn. Cắt cành dài để giữ dáng gọn.\n\n💊 Phân bón: Bón phân lân để kích hoa mỗi 2 tuần.'),

(4, 'BON-004', 'Bonsai Kim Tiền',
 'Cây Kim Tiền (Zamia) bonsai phong thủy, lá xanh bóng mượt xếp đối xứng đẹp mắt. Tượng trưng cho tài lộc, thịnh vượng, tiền bạc dồi dào. Thân cây mập mạp dự trữ nước tốt nên cực kỳ dễ chăm sóc. Chịu bóng tốt, phù hợp văn phòng, phòng khách.',
 650000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1593482892540-56b4b2a3a9b6?w=600',
 2, 35, 'Chậu sứ trắng', 'EASY', 25, true, 4, 4.6, 18,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI KIM TIỀN\n\n📍 Vị trí: Trong nhà, ánh sáng gián tiếp. Chịu bóng rất tốt.\n\n💧 Tưới nước: Tưới 1 lần/tuần. Để đất khô hoàn toàn giữa các lần tưới. Cây chịu hạn tốt, sợ úng nước.\n\n🌡️ Nhiệt độ: 18-30°C. Tránh lạnh dưới 12°C.\n\n✂️ Cắt tỉa: Ít cần cắt tỉa. Chỉ cắt lá vàng, khô.\n\n💊 Phân bón: Bón loãng mỗi 2 tháng 1 lần. Không cần bón nhiều.'),

(5, 'BON-005', 'Bonsai Phi Lao',
 'Cây Phi Lao bonsai dáng hoành, thân cây mạnh mẽ với vỏ nứt nẻ tự nhiên đầy nghệ thuật. Lá kim xanh mướt, mảnh mai bay trong gió tạo cảm giác thơ mộng. Cây có nguồn gốc Nhật Bản, được chăm sóc tỉ mỉ qua nhiều năm. Cần kinh nghiệm chăm sóc, phù hợp người chơi lâu năm.',
 5200000, 'Nhật Bản', 'Bonsai Japan Import',
 'https://images.unsplash.com/photo-1512428813834-c702c7702b78?w=600',
 12, 60, 'Chậu Nhật Bản', 'HARD', 5, true, 2, 4.9, 8,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI PHI LAO\n\n📍 Vị trí: Ngoài trời, nắng đầy đủ 6-8 giờ/ngày. Cần gió thoáng.\n\n💧 Tưới nước: Tưới mỗi ngày vào mùa hè, cách ngày vào mùa đông. Phi Lao thích đất thoát nước tốt.\n\n🌡️ Nhiệt độ: 15-35°C. Chịu được thời tiết khắc nghiệt.\n\n✂️ Cắt tỉa: Tỉa cành mỗi 2 tháng. Uốn dây đồng để tạo dáng. Cần kỹ thuật cao.\n\n🪴 Thay đất: Thay mỗi 2 năm. Dùng đất Akadama trộn cát.\n\n💊 Phân bón: Bón phân chuyên dụng bonsai mỗi 2 tuần trong mùa sinh trưởng.'),

(6, 'BON-006', 'Bonsai Đa Búp Đỏ Mini',
 'Cây Đa Búp Đỏ bonsai mini, búp non màu đỏ rực rỡ nổi bật trên nền lá xanh. Rễ khí phát triển tự nhiên tạo vẻ đẹp hoang sơ, cổ kính. Kích thước nhỏ gọn chỉ 18cm, phù hợp để bàn làm việc, kệ sách. Cây dễ sống, phù hợp người mới chơi.',
 350000, 'Việt Nam', 'Vườn Bonsai Huế',
 'https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=600',
 2, 18, 'Chậu gốm mini', 'EASY', 40, false, 3, 4.2, 25,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI ĐA BÚP ĐỎ MINI\n\n📍 Vị trí: Trong nhà, ánh sáng vừa phải. Có thể đặt ngoài trời.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần. Phun sương lên lá mỗi ngày.\n\n🌡️ Nhiệt độ: 20-32°C.\n\n✂️ Cắt tỉa: Tỉa đọt non khi cây ra nhiều cành. Giữ dáng gọn gàng.\n\n💊 Phân bón: Bón NPK loãng mỗi tháng.'),

(7, 'BON-007', 'Bonsai Lộc Vừng',
 'Cây Lộc Vừng bonsai phong thủy cao cấp, hoa đỏ rủ dài như tràng pháo tuyệt đẹp. Mang ý nghĩa lộc - tài - phúc, là cây được nhiều gia đình, doanh nhân ưa chuộng. Thân cây to khỏe, dáng cổ thụ uy nghi, vỏ xù xì trải qua 15 năm tuổi. Trưng bày trước nhà hoặc sân vườn mang lại vận may.',
 8500000, 'Việt Nam', 'Vườn Bonsai Hà Nội',
 'https://images.unsplash.com/photo-1604762524889-3e2fcc145683?w=600',
 15, 70, 'Chậu xi măng giả đá', 'MEDIUM', 3, true, 4, 4.7, 10,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI LỘC VỪNG\n\n📍 Vị trí: Ngoài trời, nắng đầy đủ. Cần không gian rộng.\n\n💧 Tưới nước: Tưới đẫm mỗi ngày. Lộc Vừng rất thích nước.\n\n🌡️ Nhiệt độ: 22-35°C. Cây nhiệt đới, không chịu lạnh.\n\n✂️ Cắt tỉa: Tỉa cành sau mùa hoa (khoảng tháng 8-9). Giữ dáng cân đối.\n\n💊 Phân bón: Bón phân lân + kali trước mùa hoa để kích hoa nở nhiều.'),

(8, 'BON-008', 'Bonsai Linh Sam',
 'Cây Linh Sam bonsai dáng bay (Fukinagashi), cành lá phát triển một phía tạo cảm giác cây bị gió thổi nghiêng rất nghệ thuật. Lá nhỏ xanh đậm, thân cây uốn lượn tinh tế. Là một trong những dáng bonsai khó tạo nhất, thể hiện đẳng cấp người chơi.',
 1800000, 'Việt Nam', 'Vườn Bonsai Đà Nẵng',
 'https://images.unsplash.com/photo-1620803366004-119b57f54cd6?w=600',
 6, 45, 'Chậu sứ xanh', 'MEDIUM', 10, false, 1, 4.4, 15,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI LINH SAM\n\n📍 Vị trí: Bán nắng, tránh nắng gắt buổi trưa. Phù hợp hiên nhà.\n\n💧 Tưới nước: Tưới mỗi ngày vào mùa nóng, cách ngày vào mùa lạnh.\n\n🌡️ Nhiệt độ: 18-32°C.\n\n✂️ Cắt tỉa: Tỉa lá định kỳ mỗi tháng để giữ dáng bay.\n\n💊 Phân bón: Bón phân hữu cơ mỗi 2 tuần.'),

(9, 'BON-009', 'Bonsai Thông Đen Nhật',
 'Cây Thông Đen Nhật Bản (Pinus thunbergii) bonsai cao cấp nhập khẩu. Dáng trực hùng vĩ (Chokkan), lá kim cứng cáp xanh đậm. Biểu tượng của sức mạnh, sự kiên cường và trường thọ trong văn hóa Nhật Bản. Cây đã qua 20 năm tạo dáng bởi nghệ nhân Nhật, là tác phẩm nghệ thuật sống.',
 12000000, 'Nhật Bản', 'Bonsai Japan Import',
 'https://images.unsplash.com/photo-1610652492500-ded49ceeb378?w=600',
 20, 50, 'Chậu Tokoname Nhật', 'HARD', 2, true, 2, 5.0, 5,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI THÔNG ĐEN NHẬT\n\n📍 Vị trí: Ngoài trời, nắng đầy đủ tối thiểu 6 giờ/ngày. Cần gió.\n\n💧 Tưới nước: Tưới khi đất khô. Tránh để đất ẩm liên tục. Dùng bình phun tia nhỏ.\n\n🌡️ Nhiệt độ: 10-30°C. Cần mùa đông lạnh để cây nghỉ ngơi.\n\n✂️ Cắt tỉa: Kỹ thuật tỉa nến (candle pruning) vào mùa xuân. Tỉa lá kim già vào mùa thu.\n\n🪴 Thay đất: Mỗi 3-5 năm. Dùng đất Akadama 100%.\n\n💊 Phân bón: Bón phân đặc biệt cho thông vào mùa xuân và thu. Không bón mùa hè nóng.\n\n⚠️ Lưu ý: Đây là loại bonsai cao cấp, cần kinh nghiệm chăm sóc. Liên hệ chúng tôi nếu cần tư vấn.'),

(10, 'BON-010', 'Bonsai Cần Thăng Mini',
 'Cây Cần Thăng bonsai mini, lá nhỏ li ti xanh mướt tạo tán dày đẹp. Dáng cây tự nhiên, dễ uốn tạo hình theo ý muốn. Cây phát triển nhanh, cho người mới chơi cảm giác thành tựu sớm. Phù hợp cho người mới bắt đầu tập chơi bonsai.',
 280000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1595351298020-038700609878?w=600',
 1, 15, 'Chậu nhựa cao cấp', 'EASY', 50, false, 3, 4.1, 30,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI CẦN THĂNG MINI\n\n📍 Vị trí: Trong nhà hoặc ngoài trời đều được. Ưa sáng nhẹ.\n\n💧 Tưới nước: Tưới mỗi ngày. Cần Thăng thích ẩm.\n\n🌡️ Nhiệt độ: 20-35°C.\n\n✂️ Cắt tỉa: Tỉa thường xuyên mỗi 2 tuần vì cây ra lá nhanh.\n\n💊 Phân bón: Bón NPK loãng mỗi 2 tuần.'),

-- === 11-20: Sản phẩm mới - Bonsai trong nhà & mini ===
(11, 'BON-011', 'Bonsai Sung Cảnh',
 'Cây Sung cảnh bonsai dáng trực đế, gốc to bè vững chãi. Lá xanh bóng hình trái tim, quả sung mọc chi chít từ thân đến cành rất đặc biệt. Trong phong thủy, Sung tượng trưng cho sự sung túc, đủ đầy. Cây dễ sống, phù hợp trưng bày phòng khách hoặc sân vườn.',
 1200000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1463936575829-25148e1db1b8?w=600',
 4, 35, 'Chậu sứ nâu', 'EASY', 20, true, 4, 4.4, 22,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI SUNG CẢNH\n\n📍 Vị trí: Nơi có ánh sáng tốt, trong nhà gần cửa sổ hoặc hiên nhà.\n\n💧 Tưới nước: Tưới đều 2-3 lần/tuần. Giữ đất ẩm nhưng không úng.\n\n🌡️ Nhiệt độ: 20-35°C. Cây nhiệt đới, không chịu lạnh.\n\n✂️ Cắt tỉa: Tỉa cành mỗi tháng. Cắt bớt quả nếu ra quá nhiều để cây không kiệt sức.\n\n💊 Phân bón: Bón phân hữu cơ mỗi tháng.'),

(12, 'BON-012', 'Bonsai Trúc Mây',
 'Cây Trúc Mây bonsai thanh thoát, thân mảnh mai uốn cong tự nhiên như dải lụa. Lá nhỏ xanh non mọc thành cụm, tạo tán lá xinh xắn. Cây mang ý nghĩa thanh cao, trong sáng. Kích thước mini phù hợp để bàn làm việc, tủ kệ trang trí.',
 320000, 'Việt Nam', 'Vườn Bonsai Đà Lạt',
 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=600',
 2, 22, 'Chậu gốm trắng mini', 'EASY', 35, false, 3, 4.0, 15,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI TRÚC MÂY\n\n📍 Vị trí: Trong nhà, ánh sáng nhẹ. Tránh nắng trực tiếp.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần. Phun sương lên lá thường xuyên.\n\n🌡️ Nhiệt độ: 18-28°C. Thích khí hậu mát.\n\n✂️ Cắt tỉa: Tỉa lá vàng, cắt ngọn khi cao quá.\n\n💊 Phân bón: Bón phân loãng mỗi tháng.'),

(13, 'BON-013', 'Bonsai Nguyệt Quế',
 'Cây Nguyệt Quế bonsai, hoa trắng nhỏ nở quanh năm tỏa hương thơm ngào ngạt. Lá xanh đậm bóng, cây phát triển chậm tạo dáng đẹp tự nhiên. Trong phong thủy, Nguyệt Quế mang lại may mắn, thăng tiến trong sự nghiệp. Đặc biệt thích hợp làm quà tặng doanh nhân.',
 780000, 'Việt Nam', 'Vườn Bonsai Huế',
 'https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=600',
 3, 30, 'Chậu sứ trắng', 'EASY', 18, true, 4, 4.5, 28,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI NGUYỆT QUẾ\n\n📍 Vị trí: Bán nắng, trong nhà gần cửa sổ hoặc ban công.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần. Giữ ẩm vừa phải.\n\n🌡️ Nhiệt độ: 20-32°C.\n\n✂️ Cắt tỉa: Tỉa sau mỗi đợt hoa tàn. Cắt cành dài để giữ dáng.\n\n💊 Phân bón: Bón phân lân mỗi 2 tuần để kích hoa.\n\n🌸 Đặc biệt: Hoa Nguyệt Quế thơm nhẹ, có thể pha trà hoặc ướp bánh.'),

(14, 'BON-014', 'Bonsai Hoa Giấy Mini',
 'Cây Hoa Giấy (Bougainvillea) bonsai mini, hoa đỏ hồng rực rỡ nở gần như quanh năm. Thân cây xù xì, gai nhỏ tạo vẻ hoang dã. Dáng cây tự nhiên phóng khoáng, mỗi mùa hoa mang đến sắc màu rực rỡ cho không gian sống.',
 550000, 'Việt Nam', 'Vườn Bonsai Nha Trang',
 'https://images.unsplash.com/photo-1518882570690-1cfe7d946d09?w=600',
 3, 25, 'Chậu đất nung', 'EASY', 22, false, 3, 4.3, 19,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI HOA GIẤY\n\n📍 Vị trí: Ngoài trời, cần nắng đầy đủ để ra hoa. Tối thiểu 5 giờ nắng/ngày.\n\n💧 Tưới nước: Tưới ít, để đất khô giữa các lần tưới. Hoa Giấy nở nhiều khi bị \"stress\" nước.\n\n🌡️ Nhiệt độ: 22-38°C. Rất chịu nóng.\n\n✂️ Cắt tỉa: Tỉa ngay sau khi hoa tàn. Cắt mạnh để kích chồi mới và hoa.\n\n💊 Phân bón: Bón phân lân + kali, ít đạm. Đạm nhiều sẽ ra lá thay vì hoa.'),

(15, 'BON-015', 'Bonsai Lựu Cảnh',
 'Cây Lựu cảnh bonsai, hoa đỏ cam rực rỡ, quả tròn đỏ mọng rất đẹp. Cây Lựu tượng trưng cho sự sinh sôi, nảy nở, con cháu đông đúc trong văn hóa phương Đông. Thân cây xù xì, vỏ bong tróc tự nhiên tạo vẻ cổ kính. Cây ra hoa quả quanh năm ở vùng nhiệt đới.',
 680000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1520412099551-62b6bafeb5bb?w=600',
 4, 32, 'Chậu gốm nâu', 'MEDIUM', 15, false, 1, 4.2, 14,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI LỰU CẢNH\n\n📍 Vị trí: Ngoài trời nắng đầy đủ. Cần 6+ giờ nắng để ra hoa quả.\n\n💧 Tưới nước: Tưới đều mỗi ngày vào mùa hè. Giảm vào mùa đông.\n\n🌡️ Nhiệt độ: 18-35°C.\n\n✂️ Cắt tỉa: Tỉa cành sau mùa quả. Không tỉa trong mùa hoa.\n\n💊 Phân bón: Bón phân kali + lân để kích hoa quả. Hạn chế đạm.'),

(16, 'BON-016', 'Bonsai Đỗ Quyên Nhật',
 'Cây Đỗ Quyên (Satsuki Azalea) bonsai Nhật Bản, hoa nở rực rỡ vào mùa xuân với sắc hồng, đỏ, trắng tuyệt đẹp. Là loại bonsai được yêu thích nhất tại Nhật Bản. Cây nhỏ nhắn nhưng khi nở hoa phủ kín tán, tạo nên cảnh tượng ngoạn mục.',
 3500000, 'Nhật Bản', 'Bonsai Japan Import',
 'https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=600',
 7, 30, 'Chậu Tokoname Nhật', 'HARD', 6, true, 6, 4.8, 9,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI ĐỖ QUYÊN NHẬT\n\n📍 Vị trí: Bán nắng, sáng nhận nắng sớm, chiều che mát. Tránh nắng gay gắt.\n\n💧 Tưới nước: Tưới mỗi ngày bằng nước mưa hoặc nước đã để qua đêm. Đỗ Quyên nhạy cảm với nước cứng.\n\n🌡️ Nhiệt độ: 10-25°C. Cần mùa đông lạnh 5-10°C trong 6-8 tuần để ra hoa.\n\n✂️ Cắt tỉa: Tỉa ngay sau khi hoa tàn. KHÔNG tỉa vào mùa thu vì nụ hoa đã hình thành.\n\n🪴 Thay đất: Mỗi 2 năm. Dùng đất Kanuma (đất chua) chuyên cho Đỗ Quyên.\n\n💊 Phân bón: Bón phân chua nhẹ, chuyên dụng cho cây ưa axit.\n\n⚠️ Lưu ý: Cây nhạy cảm, cần chăm sóc cẩn thận. Không dùng nước máy trực tiếp.'),

(17, 'BON-017', 'Bonsai Si Rễ Khí',
 'Cây Si (Ficus) bonsai với hệ thống rễ khí phát triển mạnh mẽ, buông rủ từ cành xuống đất tạo thành nhiều thân phụ ấn tượng. Tán lá rộng, xanh mướt. Là biểu tượng của sự trường tồn, che chở. Phù hợp trưng bày phòng khách rộng.',
 2200000, 'Việt Nam', 'Vườn Bonsai Hà Nội',
 'https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=600',
 6, 45, 'Chậu đá xanh', 'MEDIUM', 12, true, 1, 4.6, 16,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI SI RỄ KHÍ\n\n📍 Vị trí: Trong nhà sáng hoặc ngoài trời bán nắng.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần. Phun sương lên rễ khí mỗi ngày để rễ phát triển.\n\n🌡️ Nhiệt độ: 20-35°C. Ưa ẩm, không chịu khô.\n\n✂️ Cắt tỉa: Tỉa lá thường xuyên. Giữ rễ khí dài theo ý muốn.\n\n💊 Phân bón: Bón phân hữu cơ mỗi 2 tuần.'),

(18, 'BON-018', 'Bonsai Thiên Tuế Mini',
 'Cây Thiên Tuế (Cycas) bonsai mini, dáng thấp gốc to tròn, lá xòe ra như chiếc quạt cổ. Cây sống cực lâu, tượng trưng cho sự trường thọ, bền vững. Thiên Tuế phong thủy mang lại sự ổn định, vững chắc cho gia chủ.',
 950000, 'Việt Nam', 'Vườn Bonsai Đà Nẵng',
 'https://images.unsplash.com/photo-1509937528035-ad76b57e3c82?w=600',
 5, 25, 'Chậu sứ đen', 'EASY', 14, false, 4, 4.1, 11,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI THIÊN TUẾ MINI\n\n📍 Vị trí: Ngoài trời nắng hoặc trong nhà sáng.\n\n💧 Tưới nước: Tưới 1-2 lần/tuần. Cây chịu hạn tốt, sợ úng.\n\n🌡️ Nhiệt độ: 18-35°C.\n\n✂️ Cắt tỉa: Cắt lá già vàng ở gốc. Cây ra lá rất chậm nên ít cần tỉa.\n\n💊 Phân bón: Bón ít, 2-3 tháng/lần.'),

(19, 'BON-019', 'Bonsai Khế Cảnh',
 'Cây Khế cảnh bonsai, thân xù xì cổ kính, cành nhánh uốn lượn tự nhiên. Hoa tím hồng nhỏ xinh, quả khế vàng treo lủng lẳng rất đẹp mắt. Cây Khế trong phong thủy tượng trưng cho ngũ hành (quả 5 cánh). Dễ chăm sóc, ra hoa quả quanh năm.',
 1500000, 'Việt Nam', 'Vườn Bonsai Huế',
 'https://images.unsplash.com/photo-1501004318855-fce86ee1f1b9?w=600',
 6, 40, 'Chậu gốm men ngọc', 'MEDIUM', 10, true, 2, 4.5, 17,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI KHẾ CẢNH\n\n📍 Vị trí: Ngoài trời nắng đầy đủ để ra hoa quả tốt.\n\n💧 Tưới nước: Tưới đều mỗi ngày, giữ đất ẩm.\n\n🌡️ Nhiệt độ: 22-35°C.\n\n✂️ Cắt tỉa: Tỉa cành mỗi 2 tháng. Tỉa quả nếu ra quá nhiều.\n\n💊 Phân bón: Bón phân NPK cân đối mỗi 2 tuần.'),

(20, 'BON-020', 'Bonsai Xương Rồng Cổ Thụ',
 'Cây Xương Rồng cổ thụ bonsai, thân mập mạp chia nhiều nhánh tạo dáng cây cổ thụ độc đáo. Gai trắng mịn phủ đều, hoa vàng nhỏ nở vào mùa xuân. Cây cực kỳ dễ sống, gần như không cần chăm sóc. Mang ý nghĩa phong thủy bảo vệ, xua đuổi tà khí.',
 420000, 'Mexico', 'Cactus Garden VN',
 'https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=600',
 8, 20, 'Chậu đất nung', 'EASY', 25, false, 3, 4.0, 20,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI XƯƠNG RỒNG CỔ THỤ\n\n📍 Vị trí: Nắng đầy đủ. Để ngoài trời hoặc cửa sổ hướng nam.\n\n💧 Tưới nước: Tưới 1 lần/tuần vào mùa hè, 2 tuần/lần vào mùa đông. Tuyệt đối không để úng.\n\n🌡️ Nhiệt độ: 15-40°C. Chịu nóng cực tốt.\n\n✂️ Cắt tỉa: Gần như không cần. Cắt nhánh khô nếu có.\n\n💊 Phân bón: Bón phân xương rồng chuyên dụng 2 tháng/lần.'),

-- === 21-30: Bonsai ngoài trời & nghệ thuật ===
(21, 'BON-021', 'Bonsai Tùng Bồng Lai',
 'Cây Tùng Bồng Lai bonsai nghệ thuật, dáng trực uy nghi. Lá kim xanh đậm, mọc thành chùm tạo tán lá dày đẹp. Cây phát triển chậm, mỗi năm chỉ cao thêm vài cm nên giữ dáng lâu. Biểu tượng của sự thanh cao, bất khuất trong nghệ thuật bonsai.',
 4200000, 'Việt Nam', 'Vườn Bonsai Hà Nội',
 'https://images.unsplash.com/photo-1599598177991-ec67b5c37318?w=600',
 10, 50, 'Chậu đá mài xám', 'HARD', 7, true, 5, 4.7, 13,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI TÙNG BỒNG LAI\n\n📍 Vị trí: Ngoài trời, nắng đầy đủ. Cần gió thoáng.\n\n💧 Tưới nước: Tưới khi đất khô. Mùa hè 1 lần/ngày, mùa đông 2-3 ngày/lần.\n\n🌡️ Nhiệt độ: 10-30°C. Thích khí hậu mát.\n\n✂️ Cắt tỉa: Tỉa nến mới vào mùa xuân. Tỉa cành già vào mùa thu.\n\n🪴 Thay đất: Mỗi 3 năm. Đất thoát nước tốt.\n\n💊 Phân bón: Bón phân chuyên bonsai kim mỗi tháng trong mùa sinh trưởng.'),

(22, 'BON-022', 'Bonsai Mai Vàng',
 'Cây Mai Vàng bonsai, hoa vàng rực rỡ nở đúng dịp Tết Nguyên Đán. Thân cây sần sùi, cành nhánh uốn lượn tạo dáng cổ kính. Mai Vàng là biểu tượng mùa xuân, may mắn và thịnh vượng trong văn hóa Việt Nam. Cây có 5 cánh hoa tượng trưng phúc - lộc - thọ - khang - ninh.',
 6800000, 'Việt Nam', 'Vườn Mai Bình Định',
 'https://images.unsplash.com/photo-1490750967868-88aa4f44baee?w=600',
 12, 55, 'Chậu sứ men rạn', 'MEDIUM', 5, true, 5, 4.9, 25,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI MAI VÀNG\n\n📍 Vị trí: Ngoài trời nắng đầy đủ. Trước Tết 1 tháng đưa vào chỗ mát.\n\n💧 Tưới nước: Tưới đều mỗi ngày. Trước Tết 2 tuần giảm tưới để kích hoa.\n\n🌡️ Nhiệt độ: 22-35°C.\n\n✂️ Cắt tỉa: Lặt lá vào tháng 11-12 (âm lịch) để kích hoa nở Tết.\n\n💊 Phân bón: Bón phân kali + lân từ tháng 9 để nuôi nụ hoa.\n\n🌸 Mẹo: Muốn hoa nở đúng Tết, lặt lá trước Tết 25-30 ngày tùy thời tiết.'),

(23, 'BON-023', 'Bonsai Bách Xanh',
 'Cây Bách Xanh (Juniper) bonsai phong cách Nhật, dáng thác đổ (Kengai) ngoạn mục. Cành chính rủ xuống thấp hơn chậu, tạo cảm giác như thác nước xanh. Lá vảy nhỏ xanh đậm quanh năm. Là dáng bonsai kinh điển, thể hiện sức sống mãnh liệt vượt khó.',
 3200000, 'Nhật Bản', 'Bonsai Japan Import',
 'https://images.unsplash.com/photo-1567331711402-509c12c41959?w=600',
 8, 35, 'Chậu Nhật cao', 'HARD', 4, true, 6, 4.8, 7,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI BÁCH XANH\n\n📍 Vị trí: Ngoài trời nắng đầy đủ. Cần không gian treo hoặc kệ cao vì dáng thác đổ.\n\n💧 Tưới nước: Tưới khi đất gần khô. Dùng bình phun tia nhỏ.\n\n🌡️ Nhiệt độ: 5-28°C. Cần mùa đông lạnh.\n\n✂️ Cắt tỉa: Tỉa bằng cách ngắt đầu ngọn (pinching), không dùng kéo vì sẽ làm lá nâu.\n\n🪴 Thay đất: Mỗi 2-3 năm.\n\n💊 Phân bón: Bón phân nhẹ mỗi 2 tuần từ tháng 3-10.'),

(24, 'BON-024', 'Bonsai Sứ Thái Lan',
 'Cây Sứ Thái Lan (Adenium) bonsai, thân mập phình to ở gốc cực kỳ độc đáo. Hoa nở to đỏ hồng rực rỡ, giống hoa hồng sa mạc. Cây tích nước ở thân nên chịu hạn tốt. Dáng cây tự nhiên không cần uốn, mỗi cây mỗi dáng không trùng lặp.',
 890000, 'Thái Lan', 'Thai Bonsai Import',
 'https://images.unsplash.com/photo-1518882570690-1cfe7d946d09?w=600',
 5, 28, 'Chậu gốm men nâu', 'EASY', 18, false, 2, 4.3, 21,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI SỨ THÁI LAN\n\n📍 Vị trí: Nắng đầy đủ, tối thiểu 5 giờ/ngày.\n\n💧 Tưới nước: Tưới ít, 1-2 lần/tuần. Để đất khô hoàn toàn trước khi tưới lại. Mùa mưa che mưa.\n\n🌡️ Nhiệt độ: 25-40°C. Rất chịu nóng, sợ lạnh dưới 15°C.\n\n✂️ Cắt tỉa: Cắt cành tạo dáng vào cuối mùa đông.\n\n💊 Phân bón: Bón phân lân + kali để kích hoa. Ít đạm.\n\n⚠️ Lưu ý: Nhựa cây có độc nhẹ, rửa tay sau khi cắt tỉa.'),

(25, 'BON-025', 'Bonsai Bưởi Cảnh',
 'Cây Bưởi cảnh bonsai, thân xù xì cổ kính, quả bưởi nhỏ treo lủng lẳng quanh năm. Hoa bưởi trắng thơm ngát vào mùa xuân. Cây Bưởi trong phong thủy mang ý nghĩa no đủ, viên mãn. Đặc biệt ý nghĩa khi trưng bày dịp Tết.',
 2800000, 'Việt Nam', 'Vườn Bonsai Huế',
 'https://images.unsplash.com/photo-1501004318855-fce86ee1f1b9?w=600',
 8, 50, 'Chậu xi măng giả đá', 'MEDIUM', 6, true, 2, 4.6, 12,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI BƯỞI CẢNH\n\n📍 Vị trí: Ngoài trời nắng đầy đủ.\n\n💧 Tưới nước: Tưới đều mỗi ngày, nhất là mùa ra hoa quả.\n\n🌡️ Nhiệt độ: 22-35°C.\n\n✂️ Cắt tỉa: Tỉa cành sau mùa thu hoạch. Tỉa quả bớt để cây không kiệt.\n\n💊 Phân bón: Bón phân NPK + vi lượng mỗi 2 tuần. Bón thêm canxi cho quả chắc.'),

(26, 'BON-026', 'Bonsai Phong Lá Đỏ Nhật',
 'Cây Phong Lá Đỏ Nhật Bản (Japanese Maple) bonsai, lá chuyển từ xanh sang đỏ rực vào mùa thu tuyệt đẹp. Thân cây mảnh mai, cành nhánh chia đều tạo tán lá cân đối. Là loại bonsai được yêu thích nhất thế giới vì vẻ đẹp bốn mùa thay đổi.',
 7500000, 'Nhật Bản', 'Bonsai Japan Import',
 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600',
 10, 40, 'Chậu Tokoname men xanh', 'HARD', 3, true, 6, 5.0, 6,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI PHONG LÁ ĐỎ NHẬT\n\n📍 Vị trí: Ngoài trời, buổi sáng có nắng, buổi chiều che mát. Tránh nắng gắt.\n\n💧 Tưới nước: Tưới mỗi ngày, 2 lần vào mùa hè. Lá dễ cháy nắng nếu khô.\n\n🌡️ Nhiệt độ: 5-25°C. Cần mùa đông lạnh để lá đổi màu đẹp.\n\n✂️ Cắt tỉa: Tỉa lá vào đầu hè để ra lá mới nhỏ hơn. Tỉa cành vào mùa đông.\n\n🪴 Thay đất: Mỗi 2 năm vào đầu xuân.\n\n💊 Phân bón: Bón nhẹ, tránh bón quá nhiều.\n\n⚠️ Lưu ý: Cây cần khí hậu mát mẻ. Tại Việt Nam nên đặt ở nơi thoáng, mát.'),

(27, 'BON-027', 'Bonsai Duối Cổ Thụ',
 'Cây Duối cổ thụ bonsai, thân to sần sùi đầy vết sẹo thời gian. Lá nhỏ xanh đậm, mọc dày tạo tán tròn đẹp. Cây Duối sống rất lâu, có thể hàng trăm năm. Trong dân gian, Duối là cây thiêng, mang ý nghĩa bảo vệ gia đình.',
 3500000, 'Việt Nam', 'Vườn Bonsai Hà Nội',
 'https://images.unsplash.com/photo-1604762524889-3e2fcc145683?w=600',
 15, 45, 'Chậu đá cẩm thạch', 'MEDIUM', 5, false, 5, 4.5, 10,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI DUỐI CỔ THỤ\n\n📍 Vị trí: Ngoài trời hoặc trong nhà sáng.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần. Cây chịu hạn khá tốt.\n\n🌡️ Nhiệt độ: 18-35°C.\n\n✂️ Cắt tỉa: Tỉa lá và cành mỗi 2 tháng để giữ dáng tròn.\n\n💊 Phân bón: Bón phân hữu cơ mỗi tháng.'),

(28, 'BON-028', 'Bonsai Me Cảnh',
 'Cây Me cảnh bonsai, lá kép nhỏ xếp đều hai bên tạo vẻ thanh thoát. Thân cây xoắn vặn tự nhiên cực kỳ nghệ thuật. Quả me cong cong treo rủ rất đẹp. Cây Me dễ sống, phát triển nhanh, là lựa chọn tốt cho người mới tập chơi bonsai nghệ thuật.',
 1800000, 'Việt Nam', 'Vườn Bonsai Đà Nẵng',
 'https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=600',
 7, 42, 'Chậu gốm xanh lam', 'EASY', 12, false, 5, 4.3, 18,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI ME CẢNH\n\n📍 Vị trí: Ngoài trời nắng đầy đủ.\n\n💧 Tưới nước: Tưới mỗi ngày. Me thích ẩm.\n\n🌡️ Nhiệt độ: 22-38°C. Rất chịu nóng.\n\n✂️ Cắt tỉa: Tỉa thường xuyên vì cây ra lá nhanh. Cắt rễ mỗi năm.\n\n💊 Phân bón: Bón NPK mỗi 2 tuần. Cây phát triển mạnh nên cần bón đều.'),

(29, 'BON-029', 'Bonsai Vạn Tuế',
 'Cây Vạn Tuế bonsai mini, thân thấp mập, lá xòe thành vòng tròn xanh đậm uy nghi. Cây sống cực lâu, hàng nghìn năm tuổi ngoài tự nhiên. Tượng trưng cho sự trường thọ, bền vững, vĩnh cửu. Phù hợp tặng người lớn tuổi, đặt trước cổng nhà.',
 1200000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1509937528035-ad76b57e3c82?w=600',
 10, 30, 'Chậu xi măng tròn', 'EASY', 8, false, 4, 4.4, 14,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI VẠN TUẾ\n\n📍 Vị trí: Ngoài trời nắng hoặc bán nắng.\n\n💧 Tưới nước: Tưới 1-2 lần/tuần. Cây chịu hạn cực tốt.\n\n🌡️ Nhiệt độ: 18-38°C.\n\n✂️ Cắt tỉa: Cắt lá già úa. Cây ra lá mới rất chậm nên kiên nhẫn.\n\n💊 Phân bón: Bón ít, 3 tháng/lần.'),

(30, 'BON-030', 'Bonsai Trà Phúc Kiến',
 'Cây Trà Phúc Kiến (Fukien Tea) bonsai, lá nhỏ xanh đậm bóng, hoa trắng li ti nở quanh năm. Quả đỏ nhỏ tròn rất đẹp. Là loại bonsai trong nhà phổ biến nhất thế giới. Thân cây nhanh chóng tạo vỏ xù xì cổ kính, rất được ưa chuộng.',
 750000, 'Trung Quốc', 'Fujian Bonsai Export',
 'https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=600',
 4, 25, 'Chậu sứ men nâu', 'MEDIUM', 20, true, 1, 4.5, 30,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI TRÀ PHÚC KIẾN\n\n📍 Vị trí: Trong nhà sáng, gần cửa sổ. Tránh gió lùa.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần. Giữ ẩm đều, không để khô.\n\n🌡️ Nhiệt độ: 20-30°C. Không chịu lạnh.\n\n✂️ Cắt tỉa: Tỉa thường xuyên mỗi tháng. Cây ra lá nhanh.\n\n💊 Phân bón: Bón NPK loãng mỗi 2 tuần.\n\n⚠️ Lưu ý: Cây nhạy cảm khi thay đổi vị trí. Đặt cố định một chỗ.'),

-- === 31-40: Bonsai đa dạng ===
(31, 'BON-031', 'Bonsai Hoa Sứ Trắng',
 'Cây Hoa Sứ (Plumeria) bonsai trắng, hoa to thơm nức mũi với cánh trắng nhụy vàng thanh khiết. Thân mập mạp, cành ít nhưng hoa nở rộ tạo nên bức tranh tuyệt đẹp. Hoa Sứ gắn liền với hình ảnh ngôi chùa, mang ý nghĩa thanh tịnh, bình an.',
 580000, 'Việt Nam', 'Vườn Bonsai Nha Trang',
 'https://images.unsplash.com/photo-1520412099551-62b6bafeb5bb?w=600',
 4, 30, 'Chậu gốm trắng', 'EASY', 20, false, 1, 4.2, 16,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI HOA SỨ TRẮNG\n\n📍 Vị trí: Nắng đầy đủ, ngoài trời hoặc ban công.\n\n💧 Tưới nước: Tưới ít, 2 lần/tuần. Cây tích nước ở thân.\n\n🌡️ Nhiệt độ: 25-38°C. Rất chịu nóng.\n\n✂️ Cắt tỉa: Cắt cành tạo dáng vào cuối mùa đông khi cây rụng lá.\n\n💊 Phân bón: Bón phân lân để kích hoa.'),

(32, 'BON-032', 'Bonsai Bàng Lá Nhỏ',
 'Cây Bàng lá nhỏ (Ficus benjamina) bonsai, tán lá dày xanh mướt tạo hình ô dù tuyệt đẹp. Rễ nổi trên mặt đất tạo vẻ cổ kính. Cây rất dễ sống trong nhà, chịu bóng tốt, lọc không khí hiệu quả. Phù hợp văn phòng, phòng khách, khách sạn.',
 480000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=600',
 3, 30, 'Chậu nhựa giả gốm', 'EASY', 30, false, 1, 4.1, 22,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI BÀNG LÁ NHỎ\n\n📍 Vị trí: Trong nhà, ánh sáng gián tiếp. Lọc không khí tốt.\n\n💧 Tưới nước: Tưới 2 lần/tuần. Tránh úng nước.\n\n🌡️ Nhiệt độ: 18-30°C.\n\n✂️ Cắt tỉa: Tỉa mỗi tháng để giữ dáng tán tròn.\n\n💊 Phân bón: Bón NPK loãng mỗi tháng.\n\n⚠️ Lưu ý: Cây hay rụng lá khi thay đổi vị trí. Sẽ ra lá mới sau 2-3 tuần.'),

(33, 'BON-033', 'Bonsai Sơn Tra',
 'Cây Sơn Tra bonsai Nhật Bản, hoa trắng nhỏ nở vào xuân, quả đỏ chín vào thu đông rất đẹp. Lá nhỏ xẻ thùy, chuyển vàng cam vào mùa thu. Là bonsai bốn mùa thay đổi, mỗi mùa mang một vẻ đẹp riêng. Rất phổ biến trong nghệ thuật bonsai Nhật.',
 4800000, 'Nhật Bản', 'Bonsai Japan Import',
 'https://images.unsplash.com/photo-1490750967868-88aa4f44baee?w=600',
 9, 35, 'Chậu Tokoname nâu', 'HARD', 4, true, 6, 4.7, 8,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI SƠN TRA\n\n📍 Vị trí: Ngoài trời, nắng sáng. Che nắng buổi chiều mùa hè.\n\n💧 Tưới nước: Tưới đều mỗi ngày, 2 lần vào mùa hè. Không để khô.\n\n🌡️ Nhiệt độ: 5-25°C. Cần mùa đông lạnh để ra hoa.\n\n✂️ Cắt tỉa: Tỉa sau khi hoa tàn. Tỉa cành vào mùa đông.\n\n💊 Phân bón: Bón phân cân đối sau khi hoa tàn và vào mùa thu.'),

(34, 'BON-034', 'Bonsai Cây Đề',
 'Cây Đề (Bồ Đề) bonsai, lá hình trái tim đầu nhọn đặc trưng. Cây Đề là cây thiêng trong Phật giáo - nơi Đức Phật giác ngộ. Mang ý nghĩa giác ngộ, trí tuệ, bình an. Rễ khí phát triển mạnh, thân cây uy nghi.',
 2500000, 'Ấn Độ', 'Bodhi Bonsai',
 'https://images.unsplash.com/photo-1463936575829-25148e1db1b8?w=600',
 7, 45, 'Chậu sứ nâu đất', 'MEDIUM', 8, false, 4, 4.6, 11,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI CÂY ĐỀ\n\n📍 Vị trí: Trong nhà sáng hoặc ngoài trời bán nắng.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần. Phun sương lên lá.\n\n🌡️ Nhiệt độ: 20-35°C. Cây nhiệt đới.\n\n✂️ Cắt tỉa: Tỉa lá định kỳ. Lá Đề to nên tỉa thường xuyên để lá nhỏ lại.\n\n💊 Phân bón: Bón phân hữu cơ mỗi 2 tuần.'),

(35, 'BON-035', 'Bonsai Sen Đá Cổ Thụ',
 'Bộ sưu tập Sen Đá (Echeveria) dáng cổ thụ, nhiều đầu xếp thành chùm như bông hoa. Thân gỗ hóa tạo dáng bonsai thu nhỏ rất đáng yêu. Cực kỳ dễ chăm sóc, gần như không cần tưới nước. Phù hợp để bàn làm việc, làm quà tặng.',
 250000, 'Hàn Quốc', 'Korean Succulent',
 'https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=600',
 3, 12, 'Chậu sứ mini trắng', 'EASY', 45, false, 3, 4.0, 35,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI SEN ĐÁ CỔ THỤ\n\n📍 Vị trí: Nắng nhẹ buổi sáng hoặc ánh sáng gián tiếp.\n\n💧 Tưới nước: Tưới 1 lần/tuần vào mùa hè, 2 tuần/lần vào mùa đông. Tưới vào gốc, tránh ướt lá.\n\n🌡️ Nhiệt độ: 15-30°C.\n\n✂️ Cắt tỉa: Cắt lá già phía dưới. Tách đầu mới để nhân giống.\n\n💊 Phân bón: Bón phân sen đá chuyên dụng mỗi 2 tháng.'),

(36, 'BON-036', 'Bonsai Hồng Ngọc Mai',
 'Cây Hồng Ngọc Mai bonsai, búp non đỏ rực như ngọc hồng, lá trưởng thành xanh đậm bóng. Cây ra búp đỏ quanh năm, luôn có sự tương phản đỏ - xanh rất đẹp. Hoa nhỏ trắng thơm nhẹ. Phù hợp trưng bày trong nhà, mang vẻ đẹp quý phái.',
 520000, 'Việt Nam', 'Vườn Bonsai Đà Lạt',
 'https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=600',
 3, 22, 'Chậu gốm men ngọc', 'EASY', 28, false, 1, 4.3, 19,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI HỒNG NGỌC MAI\n\n📍 Vị trí: Trong nhà sáng hoặc ban công.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần. Giữ ẩm vừa.\n\n🌡️ Nhiệt độ: 20-32°C.\n\n✂️ Cắt tỉa: Tỉa ngọn để kích búp đỏ mới. Càng tỉa càng đẹp.\n\n💊 Phân bón: Bón NPK mỗi 2 tuần.'),

(37, 'BON-037', 'Bonsai Thông 5 Lá Nhật',
 'Cây Thông 5 Lá Nhật Bản (Goyomatsu / Pinus parviflora) bonsai cao cấp. Lá kim mọc thành chùm 5 lá, ngắn và mềm hơn thông đen. Tán cây xanh xám nhạt đặc trưng, rất thanh thoát. Là loại thông bonsai quý nhất, biểu tượng của sự hòa hợp.',
 15000000, 'Nhật Bản', 'Bonsai Japan Import',
 'https://images.unsplash.com/photo-1610652492500-ded49ceeb378?w=600',
 25, 55, 'Chậu Tokoname cổ', 'HARD', 2, true, 6, 5.0, 4,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI THÔNG 5 LÁ NHẬT\n\n📍 Vị trí: Ngoài trời, nắng đầy đủ. Cần gió thoáng.\n\n💧 Tưới nước: Tưới khi đất khô. Ít nước hơn thông đen. Thoát nước tốt.\n\n🌡️ Nhiệt độ: 5-25°C. Cần mùa đông lạnh.\n\n✂️ Cắt tỉa: Tỉa nến vào mùa xuân. Tỉa lá kim già vào mùa thu.\n\n🪴 Thay đất: Mỗi 3-5 năm. Dùng Akadama + Pumice.\n\n💊 Phân bón: Bón nhẹ từ tháng 3-6 và tháng 9-11.\n\n⚠️ Đây là bonsai đỉnh cao, cần kinh nghiệm nhiều năm. Liên hệ tư vấn.'),

(38, 'BON-038', 'Bonsai Cây Mận',
 'Cây Mận bonsai, hoa trắng hồng nở rộ vào cuối đông đầu xuân, trước khi ra lá. Cánh hoa mỏng manh bay trong gió xuân rất lãng mạn. Quả mận nhỏ xanh treo cành đẹp mắt. Cây Mận tượng trưng cho sự kiên cường, nở hoa trong giá rét.',
 2800000, 'Nhật Bản', 'Bonsai Japan Import',
 'https://images.unsplash.com/photo-1520412099551-62b6bafeb5bb?w=600',
 8, 38, 'Chậu Nhật men đen', 'MEDIUM', 5, false, 6, 4.6, 9,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI CÂY MẬN\n\n📍 Vị trí: Ngoài trời, nắng đầy đủ.\n\n💧 Tưới nước: Tưới đều mỗi ngày vào mùa sinh trưởng.\n\n🌡️ Nhiệt độ: 5-28°C. Cần lạnh để ra hoa.\n\n✂️ Cắt tỉa: Tỉa cành ngay sau khi hoa tàn. Không tỉa vào thu vì nụ đang hình thành.\n\n💊 Phân bón: Bón phân cân đối sau hoa tàn.'),

(39, 'BON-039', 'Bonsai Tràm Trà',
 'Cây Tràm Trà (Melaleuca) bonsai, vỏ cây bong tróc nhiều lớp tạo vẻ cổ kính độc đáo. Lá nhỏ xanh thẫm, có mùi thơm dễ chịu khi vò. Hoa trắng nhỏ mọc thành chùm. Cây dễ sống, chịu được nhiều điều kiện môi trường khác nhau.',
 980000, 'Úc', 'Aussie Bonsai',
 'https://images.unsplash.com/photo-1620803366004-119b57f54cd6?w=600',
 5, 35, 'Chậu gốm nâu đất', 'EASY', 15, false, 2, 4.2, 12,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI TRÀM TRÀ\n\n📍 Vị trí: Ngoài trời hoặc trong nhà sáng.\n\n💧 Tưới nước: Tưới đều 2-3 lần/tuần. Cây thích ẩm.\n\n🌡️ Nhiệt độ: 15-35°C.\n\n✂️ Cắt tỉa: Tỉa cành mỗi tháng. Cây ra cành nhanh.\n\n💊 Phân bón: Bón NPK mỗi 2 tuần.'),

(40, 'BON-040', 'Bonsai Hoa Mai Trắng',
 'Cây Mai Trắng bonsai, hoa 5 cánh trắng tinh khiết nở vào dịp Tết. Hương thơm nhẹ nhàng, thanh tao. Mai Trắng quý hiếm hơn Mai Vàng, mang ý nghĩa thanh cao, thuần khiết. Thân cây cổ thụ, dáng bay nghệ thuật.',
 9500000, 'Việt Nam', 'Vườn Mai Bình Định',
 'https://images.unsplash.com/photo-1490750967868-88aa4f44baee?w=600',
 15, 50, 'Chậu sứ cổ', 'HARD', 2, true, 5, 4.9, 6,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI HOA MAI TRẮNG\n\n📍 Vị trí: Ngoài trời nắng đầy đủ.\n\n💧 Tưới nước: Tưới đều mỗi ngày. Giảm trước Tết 2 tuần.\n\n🌡️ Nhiệt độ: 22-35°C.\n\n✂️ Cắt tỉa: Lặt lá trước Tết 25-30 ngày. Tỉa cành sau Tết.\n\n💊 Phân bón: Bón kali + lân từ tháng 9 để nuôi nụ.\n\n⚠️ Mai Trắng quý hiếm, cần chăm sóc cẩn thận. Liên hệ tư vấn.'),

-- === 41-45: Bonsai đặc biệt ===
(41, 'BON-041', 'Bonsai Thủy Tùng',
 'Cây Thủy Tùng (Bald Cypress) bonsai, loài cây cổ đại sống gần nước. Rễ khí nổi lên mặt đất (rễ thở) tạo vẻ độc đáo không loài nào có. Lá kim mềm chuyển vàng cam tuyệt đẹp vào mùa thu trước khi rụng. Dáng cây hùng vĩ, uy nghi.',
 6500000, 'Mỹ', 'American Bonsai Import',
 'https://images.unsplash.com/photo-1512428813834-c702c7702b78?w=600',
 10, 50, 'Chậu lớn men xanh', 'HARD', 3, true, 5, 4.8, 5,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI THỦY TÙNG\n\n📍 Vị trí: Ngoài trời, nắng đầy đủ. Có thể đặt chậu trong khay nước.\n\n💧 Tưới nước: Tưới rất nhiều! Có thể ngâm chậu trong khay nước 2-3cm. Thủy Tùng thích nước.\n\n🌡️ Nhiệt độ: 5-30°C. Cần mùa đông lạnh.\n\n✂️ Cắt tỉa: Tỉa cành vào mùa sinh trưởng. Cây rụng lá vào đông là bình thường.\n\n💊 Phân bón: Bón đều từ xuân đến thu.'),

(42, 'BON-042', 'Bonsai Cây Bông Trang',
 'Cây Bông Trang (Ixora) bonsai, hoa mọc thành chùm tròn với nhiều màu sắc: đỏ, cam, vàng, hồng. Hoa nở gần như quanh năm ở vùng nhiệt đới. Lá xanh bóng, dáng cây gọn gàng. Rất phổ biến trong văn hóa bonsai Việt Nam.',
 450000, 'Việt Nam', 'Vườn Bonsai Sài Gòn',
 'https://images.unsplash.com/photo-1518882570690-1cfe7d946d09?w=600',
 3, 25, 'Chậu gốm nâu', 'EASY', 25, false, 1, 4.1, 20,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI BÔNG TRANG\n\n📍 Vị trí: Nắng đầy đủ để ra hoa nhiều.\n\n💧 Tưới nước: Tưới mỗi ngày. Giữ ẩm đều.\n\n🌡️ Nhiệt độ: 22-35°C.\n\n✂️ Cắt tỉa: Tỉa sau mỗi đợt hoa tàn để kích chồi hoa mới.\n\n💊 Phân bón: Bón phân lân để kích hoa. Mỗi 2 tuần.'),

(43, 'BON-043', 'Bonsai Tùng Kim Cương',
 'Cây Tùng Kim Cương bonsai mini, lá kim nhỏ xếp chặt tạo tán lá dày đặc như viên kim cương xanh. Cây phát triển rất chậm, giữ dáng lâu. Kích thước mini phù hợp để bàn. Là lựa chọn sang trọng cho không gian làm việc.',
 680000, 'Việt Nam', 'Vườn Bonsai Đà Lạt',
 'https://images.unsplash.com/photo-1595351298020-038700609878?w=600',
 4, 15, 'Chậu sứ xanh mini', 'MEDIUM', 20, true, 3, 4.4, 23,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI TÙNG KIM CƯƠNG\n\n📍 Vị trí: Ánh sáng gián tiếp hoặc nắng nhẹ buổi sáng.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần. Phun sương lên lá.\n\n🌡️ Nhiệt độ: 15-28°C. Thích mát.\n\n✂️ Cắt tỉa: Tỉa nhẹ mỗi 2-3 tháng. Cây chậm lớn nên ít cần tỉa.\n\n💊 Phân bón: Bón loãng mỗi tháng.'),

(44, 'BON-044', 'Bonsai Táo Cảnh',
 'Cây Táo cảnh bonsai, quả táo nhỏ đỏ mọng treo lủng lẳng quanh năm rất bắt mắt. Hoa trắng nhỏ thơm nhẹ nở vào mùa xuân. Cây Táo mang ý nghĩa bình an (苹安 - Píng ān). Phù hợp trưng bày phòng khách, làm quà tặng tân gia.',
 1200000, 'Trung Quốc', 'Oriental Bonsai',
 'https://images.unsplash.com/photo-1501004318855-fce86ee1f1b9?w=600',
 5, 35, 'Chậu sứ đỏ', 'MEDIUM', 10, false, 4, 4.3, 15,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI TÁO CẢNH\n\n📍 Vị trí: Ngoài trời nắng đầy đủ.\n\n💧 Tưới nước: Tưới đều mỗi ngày, nhất là mùa ra quả.\n\n🌡️ Nhiệt độ: 15-30°C. Cần thời tiết mát.\n\n✂️ Cắt tỉa: Tỉa cành sau mùa quả. Giữ 3-5 quả/cành.\n\n💊 Phân bón: Bón NPK + vi lượng mỗi 2 tuần. Bón canxi cho quả chắc.'),

(45, 'BON-045', 'Bonsai Trắc Bách Diệp',
 'Cây Trắc Bách Diệp (Thuja) bonsai, lá vảy xanh đậm mọc xếp lớp tạo tán hình nón hoặc tròn đẹp mắt. Cây tỏa mùi thơm nhẹ dễ chịu, có tác dụng đuổi muỗi tự nhiên. Trong phong thủy, Trắc Bách Diệp mang năng lượng tích cực, xua tà khí.',
 850000, 'Việt Nam', 'Vườn Bonsai Đà Lạt',
 'https://images.unsplash.com/photo-1599598177991-ec67b5c37318?w=600',
 4, 30, 'Chậu sứ nâu', 'MEDIUM', 16, false, 1, 4.3, 13,
 '🌿 HƯỚNG DẪN CHĂM SÓC BONSAI TRẮC BÁCH DIỆP\n\n📍 Vị trí: Ngoài trời hoặc ban công nắng sáng.\n\n💧 Tưới nước: Tưới 2-3 lần/tuần. Giữ ẩm vừa phải.\n\n🌡️ Nhiệt độ: 10-30°C. Thích mát.\n\n✂️ Cắt tỉa: Tỉa bằng tay (pinching) mỗi tháng để giữ dáng chặt.\n\n💊 Phân bón: Bón NPK loãng mỗi 2 tuần trong mùa sinh trưởng.');
