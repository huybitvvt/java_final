-- ============================================
-- FILE BỔ SUNG DỮ LIỆU CHO CƠ SỞ DỮ LIỆU
-- Chạy file này sau database.sql để thêm dữ liệu mẫu
-- ============================================

USE QuanLyCuaHangSach;
GO

-- ============================================
-- BỔ SUNG NHÂN VIÊN (thêm 17 nhân viên - tổng 20)
-- ============================================
SET IDENTITY_INSERT NhanVien ON;
INSERT INTO NhanVien (MaNhanVien, HoTen, GioiTinh, NgaySinh, SoDienThoai, Email, DiaChi, ChucVu, Luong, NgayVaoLam, TrangThai)
VALUES
    (13, N'Phạm Văn M', N'Nam', '1993-04-15', '0934567891', 'pvm@bookstore.com', N'Hà Nội', N'Nhân viên', 7000000, '2023-03-01', N'Hoạt động'),
    (14, N'Vũ Thị N', N'Nữ', '1996-07-22', '0945678902', 'vtn@bookstore.com', N'TP.HCM', N'Nhân viên', 6800000, '2023-05-15', N'Hoạt động'),
    (15, N'Đặng Văn P', N'Nam', '1994-11-08', '0956789013', 'dvp@bookstore.com', N'Đà Nẵng', N'Thu ngân', 8200000, '2022-08-20', N'Hoạt động'),
    (16, N'Bùi Thị Q', N'Nữ', '1997-02-14', '0967890124', 'btq@bookstore.com', N'Hải Phòng', N'Nhân viên', 6500000, '2023-07-01', N'Hoạt động'),
    (17, N'Hoàng Văn R', N'Nam', '1991-09-30', '0978901235', 'hvr@bookstore.com', N'Cần Thơ', N'Nhân viên', 7500000, '2021-10-10', N'Hoạt động'),
    (18, N'Ngô Thị S', N'Nữ', '1995-06-18', '0989012346', 'nts@bookstore.com', N'Hà Nội', N'Thu ngân', 8000000, '2022-04-05', N'Hoạt động'),
    (19, N'Phạm Thị T', N'Nữ', '1998-12-25', '0990123457', 'ptt@bookstore.com', N'TP.HCM', N'Nhân viên', 6200000, '2023-09-01', N'Hoạt động'),
    (20, N'Trần Văn U', N'Nam', '1992-03-12', '0901234568', 'tvu@bookstore.com', N'Đà Nẵng', N'Quản lý kho', 9500000, '2021-01-20', N'Hoạt động');
SET IDENTITY_INSERT NhanVien OFF;
GO

-- ============================================
-- BỔ SUNG KHÁCH HÀNG (thêm 65 khách - tổng 100)
-- ============================================
SET IDENTITY_INSERT KhachHang ON;
INSERT INTO KhachHang (MaKhachHang, HoTen, GioiTinh, NgaySinh, SoDienThoai, Email, DiaChi, DiemTichLuy)
VALUES
    (36, N'Vũ Thị Mai', N'Nữ', '1997-04-22', '0990123450', 'vtmai@email.com', N'Hà Nội', 145),
    (37, N'Đặng Văn Hùng', N'Nam', '1993-08-15', '0991123451', 'dvhung@email.com', N'TP.HCM', 195),
    (38, N'Bùi Thị Lan', N'Nữ', '1996-12-08', '0992123452', 'btlan@email.com', N'Đà Nẵng', 88),
    (39, N'Hoàng Văn Nam', N'Nam', '1991-05-30', '0993123453', 'hvnam@email.com', N'Hải Phòng', 230),
    (40, N'Ngô Thị Thy', N'Nữ', '1999-09-12', '0994123454', 'ntthy@email.com', N'Cần Thơ', 42),
    (41, N'Phạm Văn Đạt', N'Nam', '1994-02-25', '0995123455', 'pvdat@email.com', N'Hà Nội', 310),
    (42, N'Trần Thị Ngọc', N'Nữ', '1995-07-18', '0996123456', 'ttngoc@email.com', N'TP.HCM', 175),
    (43, N'Lê Văn Bảo', N'Nam', '1992-10-05', '0997123457', 'lvbao@email.com', N'Đà Nẵng', 265),
    (44, N'Vũ Thị Hồng', N'Nữ', '1998-03-28', '0998123458', 'vthong@email.com', N'Hải Phòng', 78),
    (45, N'Đặng Văn Khoa', N'Nam', '1990-11-20', '0999123459', 'dvkhoa@email.com', N'Cần Thơ', 340),
    (46, N'Bùi Thị Minh', N'Nữ', '1993-06-14', '0900123460', 'btminh@email.com', N'Hà Nội', 198),
    (47, N'Hoàng Văn Sơn', N'Nam', '1997-01-08', '0901123461', 'hvson@email.com', N'TP.HCM', 125),
    (48, N'Ngô Thị Yến', N'Nữ', '1994-08-22', '0902123462', 'ntyen@email.com', N'Đà Nẵng', 255),
    (49, N'Phạm Văn Thành', N'Nam', '1991-12-30', '0903123463', 'pvthanh@email.com', N'Hải Phòng', 290),
    (50, N'Trần Thị Quỳnh', N'Nữ', '1999-05-15', '0904123464', 'ttquynh@email.com', N'Cần Thơ', 65),
    (51, N'Lê Văn Hậu', N'Nam', '1992-09-08', '0905123465', 'lvhau@email.com', N'Hà Nội', 380),
    (52, N'Vũ Thị Anh', N'Nữ', '1996-04-18', '0906123466', 'vtanh@email.com', N'TP.HCM', 142),
    (53, N'Đặng Văn Trung', N'Nam', '1993-11-25', '0907123467', 'dvtrung@email.com', N'Đà Nẵng', 215),
    (54, N'Bùi Thị Hà', N'Nữ', '1998-07-12', '0908123468', 'btha@email.com', N'Hải Phòng', 95),
    (55, N'Hoàng Văn Tuấn', N'Nam', '1990-02-05', '0909123469', 'hvtuan@email.com', N'Cần Thơ', 425),
    (56, N'Ngô Thị Linh', N'Nữ', '1995-10-22', '0910123470', 'ntlinh@email.com', N'Hà Nội', 188),
    (57, N'Phạm Văn Hải', N'Nam', '1994-06-08', '0911123471', 'pvhai@email.com', N'TP.HCM', 275),
    (58, N'Trần Văn Dũng', N'Nam', '1991-03-15', '0912123472', 'tvdung@email.com', N'Đà Nẵng', 320),
    (59, N'Lê Thị Thu', N'Nữ', '1997-12-28', '0913123473', 'ltthu@email.com', N'Hải Phòng', 112),
    (60, N'Vũ Văn Đức', N'Nam', '1992-08-10', '0914123474', 'vvduc@email.com', N'Cần Thơ', 245),
    (61, N'Đặng Thị Hoa', N'Nữ', '1999-04-05', '0915123475', 'dthoa@email.com', N'Hà Nội', 55),
    (62, N'Bùi Văn Minh', N'Nam', '1993-09-18', '0916123476', 'bvminh@email.com', N'TP.HCM', 365),
    (63, N'Hoàng Thị Ngân', N'Nữ', '1996-01-22', '0917123477', 'htngan@email.com', N'Đà Nẵng', 158),
    (64, N'Ngô Văn Lâm', N'Nam', '1991-07-30', '0918123478', 'nvlam@email.com', N'Hải Phòng', 285),
    (65, N'Phạm Thị Thảo', N'Nữ', '1998-11-12', '0919123479', 'ptthao@email.com', N'Cần Thơ', 82),
    (66, N'Trần Văn Hiếu', N'Nam', '1994-05-08', '0920123480', 'tvhieu@email.com', N'Hà Nội', 335),
    (67, N'Lê Thị My', N'Nữ', '1995-08-25', '0921123481', 'ltmy@email.com', N'TP.HCM', 195),
    (68, N'Vũ Văn Phước', N'Nam', '1992-02-14', '0922123482', 'vvphuoc@email.com', N'Đà Nẵng', 410),
    (69, N'Đặng Thị Phương', N'Nữ', '1997-06-18', '0923123483', 'dtphuong@email.com', N'Hải Phòng', 128),
    (70, N'Bùi Văn Thịnh', N'Nam', '1990-12-05', '0924123484', 'bvthinh@email.com', N'Cần Thơ', 295),
    (71, N'Hoàng Văn Đạt', N'Nam', '1993-04-22', '0925123485', 'hvdat@email.com', N'Hà Nội', 178),
    (72, N'Ngô Thị Hà', N'Nữ', '1996-09-30', '0926123486', 'ntha@email.com', N'TP.HCM', 225),
    (73, N'Phạm Văn Cường', N'Nam', '1991-11-15', '0927123487', 'pvcuong@email.com', N'Đà Nẵng', 355),
    (74, N'Trần Thị Bích', N'Nữ', '1999-03-08', '0928123488', 'ttbich@email.com', N'Hải Phòng', 72),
    (75, N'Lê Văn Thắng', N'Nam', '1994-07-20', '0929123489', 'lvthang@email.com', N'Cần Thơ', 268),
    (76, N'Vũ Thị Thủy', N'Nữ', '1995-12-12', '0930123490', 'vtthuy@email.com', N'Hà Nội', 185),
    (77, N'Đặng Văn Long', N'Nam', '1992-06-28', '0931123491', 'dvlong@email.com', N'TP.HCM', 312),
    (78, N'Bùi Thị Hương', N'Nữ', '1998-10-05', '0932123492', 'bthuong@email.com', N'Đà Nẵng', 98),
    (79, N'Hoàng Văn Bảo', N'Nam', '1990-01-18', '0933123493', 'hvbao@email.com', N'Hải Phòng', 445),
    (80, N'Ngô Thị Mai', N'Nữ', '1997-05-22', '0934123494', 'ntmai@email.com', N'Cần Thơ', 165),
    (81, N'Phạm Văn Tài', N'Nam', '1993-08-08', '0935123495', 'pvtai@email.com', N'Hà Nội', 238),
    (82, N'Trần Thị Hạnh', N'Nữ', '1996-02-28', '0936123496', 'tthanh@email.com', N'TP.HCM', 142),
    (83, N'Lê Văn Khôi', N'Nam', '1991-09-15', '0937123497', 'lvkhoi@email.com', N'Đà Nẵng', 298),
    (84, N'Vũ Thị Ngọc', N'Nữ', '1994-04-10', '0938123498', 'vtngoc@email.com', N'Hải Phòng', 175),
    (85, N'Đặng Văn Hòa', N'Nam', '1999-07-25', '0939123499', 'dvhoa@email.com', N'Cần Thơ', 48),
    (86, N'Bùi Thị Uyên', N'Nữ', '1992-11-30', '0940123500', 'btuyen@email.com', N'Hà Nội', 215),
    (87, N'Hoàng Văn Kiệt', N'Nam', '1995-03-18', '0941123501', 'hvkiet@email.com', N'TP.HCM', 328),
    (88, N'Ngô Thị Trang', N'Nữ', '1998-08-12', '0942123502', 'nttrang@email.com', N'Đà Nẵng', 85),
    (89, N'Phạm Văn Đức', N'Nam', '1990-06-05', '0943123503', 'pvduc@email.com', N'Hải Phòng', 385),
    (90, N'Trần Thị Liên', N'Nữ', '1993-12-22', '0944123504', 'ttlien@email.com', N'Cần Thơ', 158),
    (91, N'Lê Văn Phát', N'Nam', '1997-02-08', '0945123505', 'lvphat@email.com', N'Hà Nội', 198),
    (92, N'Vũ Thị Hạnh', N'Nữ', '1994-09-28', '0946123506', 'vthanh2@email.com', N'TP.HCM', 245),
    (93, N'Đặng Văn Tuấn', N'Nam', '1991-05-14', '0947123507', 'dvtuan2@email.com', N'Đà Nẵng', 275),
    (94, N'Bùi Thị Vân', N'Nữ', '1996-10-20', '0948123508', 'btvan@email.com', N'Hải Phòng', 132),
    (95, N'Hoàng Văn Nhân', N'Nam', '1992-07-08', '0949123509', 'hvnhân@email.com', N'Cần Thơ', 312),
    (96, N'Ngô Thị Kim', N'Nữ', '1999-01-25', '0950123510', 'ntkim@email.com', N'Hà Nội', 62),
    (97, N'Phạm Văn Sơn', N'Nam', '1993-04-12', '0951123511', 'pvson2@email.com', N'TP.HCM', 228),
    (98, N'Trần Thị Nga', N'Nữ', '1995-11-18', '0952123512', 'ttnga@email.com', N'Đà Nẵng', 175),
    (99, N'Lê Văn Thọ', N'Nam', '1990-08-30', '0953123513', 'lvtho@email.com', N'Hải Phòng', 398),
    (100, N'Vũ Thị Lan', N'Nữ', '1997-06-15', '0954123514', 'vtlan@email.com', N'Cần Thơ', 115);
SET IDENTITY_INSERT KhachHang OFF;
GO

-- ============================================
-- BỔ SUNG NHÀ CUNG CẤP (thêm 8 nhà cung cấp - tổng 20)
-- ============================================
SET IDENTITY_INSERT NhaCungCap ON;
INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SoDienThoai, Email, NguoiLienHe, MoTa)
VALUES
    (13, N'Nhà xuất bản Lao động', N'Hà Nội', '02438221121', 'nxbld@nxbld.vn', N'Vũ Văn D', N'NXB sách kỹ năng và lao động'),
    (14, N'Nhà xuất bản Chính trị Quốc gia', N'Hà Nội', '02438221122', 'nxbct@nxbct.vn', N'Đặng Thị E', N'NXB sách chính trị và pháp luật'),
    (15, N'Nhà xuất bản Đại học Sư phạm', N'TP.HCM', '02838221123', 'nxbdhsp@dhsp.vn', N'Bùi Văn F', N'NXB sách giáo dục đại học'),
    (16, N'Nhà xuất bản Thông tin và Truyền thông', N'Hà Nội', '02438221124', 'nxbtt@nxbtt.vn', N'Hoàng Thị G', N'NXB sách công nghệ thông tin'),
    (17, N'Nhà xuất bản Xây dựng', N'Hà Nội', '02438221125', 'nxbxd@nxbxd.vn', N'Ngô Văn H', N'NXB sách xây dựng và kiến trúc'),
    (18, N'Nhà xuất bản Y học', N'TP.HCM', '02838221126', 'nxbyh@nxbyh.vn', N'Phạm Thị I', N'NXB sách y học và sức khỏe'),
    (19, N'Nhà xuất bản Nông nghiệp', N'Hà Nội', '02438221127', 'nxbnn@nxbnn.vn', N'Trần Văn J', N'NXB sách nông nghiệp và sinh học'),
    (20, N'Nhà xuất bản Giao thông Vận tải', N'Hà Nội', '02438221128', 'nxbgt@nxbgt.vn', N'Lê Thị K', N'NXB sách giao thông và vận tải');
SET IDENTITY_INSERT NhaCungCap OFF;
GO

-- ============================================
-- BỔ SUNG SÁCH (tổng cộng 120 cuốn)
-- ============================================
SET IDENTITY_INSERT Sach ON;
INSERT INTO Sach (MaSach, TenSach, TacGia, NhaXuatBan, TheLoai, NamXuatBan, GiaBia, SoLuongTon, MoTa)
VALUES
    (1, N'Lập trình Java cơ bản', N'Nguyễn Văn A', N'Nhà xuất bản Khoa học', N'Công nghệ', 2023, 89000, 120, N'Học Java từ cơ bản đến nâng cao'),
    (2, N'Python cho người mới bắt đầu', N'Trần Thị B', N'Nhà xuất bản Khoa học', N'Công nghệ', 2023, 79000, 150, N'Python cơ bản cho beginners'),
    (3, N'Cấu trúc dữ liệu giải thuật', N'Lê Văn C', N'Nhà xuất bản Giáo dục', N'Giáo khoa', 2022, 95000, 80, N'Giáo trình CNTT'),
    (4, N'Marketing đỉnh cao', N'Nguyễn Thị D', N'Nhà xuất bản Tài chính', N'Kinh tế', 2024, 120000, 95, N'Chiến lược marketing hiệu quả'),
    (5, N'Tiếng Anh giao tiếp', N'Hoàng Văn E', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2023, 85000, 200, N'Tiếng Anh thực hành'),
    (6, N'Kế toán doanh nghiệp', N'Vũ Thị F', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 150000, 65, N'Hướng dẫn kế toán chi tiết'),
    (7, N'Thiết kế đồ họa', N'Đặng Văn G', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2024, 180000, 55, N'Học thiết kế đồ họa chuyên nghiệp'),
    (8, N'Nấu ăn ngon', N'Bùi Thị H', N'Nhà xuất bản Ẩm thực', N'Ẩm thực', 2023, 95000, 140, N'Công thức nấu ăn Việt Nam');

-- Thêm sách mới (ID 56-120)
INSERT INTO Sach (MaSach, TenSach, TacGia, NhaXuatBan, TheLoai, NamXuatBan, GiaBia, SoLuongTon, MoTa)
VALUES
    (56, N'Lập trình Spring Boot', N'Phạm Văn M', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 165000, 60, N'Lập trình Java Spring Boot'),
    (57, N'Tiếng Nhật N3', N'Nguyễn Thị N', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2023, 95000, 85, N'Tiếng Nhật trung cấp'),
    (58, N'Lịch sử Việt Nam', N'Trần Văn O', N'Nhà xuất bản Văn hóa', N'Lịch sử', 2022, 88000, 75, N'Lịch sử dân tộc Việt Nam'),
    (59, N'Đầu tư chứng khoán', N'Lê Thị P', N'Nhà xuất bản Tài chính', N'Kinh tế', 2024, 135000, 90, N'Kiến thức đầu tư tài chính'),
    (60, N'Photoshop cơ bản', N'Vũ Văn Q', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2023, 125000, 70, N'Học Photoshop từ đầu'),
    (61, N'Ẩm thực Châu Á', N'Đặng Thị R', N'Nhà xuất bản Ẩm thực', N'Ẩm thực', 2024, 105000, 95, N'Công thức món Á'),
    (62, N'Lập trình ReactJS', N'Bùi Văn S', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 142000, 65, N'Lập trình Frontend với React'),
    (63, N'Tiếng Trung HSK 4', N'Hoàng Thị T', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2023, 88000, 110, N'Tiếng Trung trung cấp'),
    (64, N'Giáo dục thể chất', N'Ngô Văn U', N'Nhà xuất bản Thể thao', N'Thể thao', 2022, 65000, 120, N'Giáo trình thể dục'),
    (65, N'Phong thủy căn nhà', N'Phạm Thị V', N'Nhà xuất bản Văn hóa', N'Đời sống', 2023, 75000, 85, N'Phong thủy trong cuộc sống'),
    (66, N'Lập trình VueJS', N'Trần Văn W', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 128000, 55, N'Framework Vue.js'),
    (67, N'Kinh doanh online', N'Lê Thị X', N'Nhà xuất bản Tài chính', N'Kinh tế', 2024, 98000, 140, N'Bán hàng trực tuyến'),
    (68, N'Nhiếp ảnh nâng cao', N'Vũ Văn Y', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2023, 155000, 45, N'Nhiếp ảnh chuyên nghiệp'),
    (69, N'Yoga thiền định', N'Đặng Thị Z', N'Nhà xuất bản Thể thao', N'Sức khỏe', 2024, 82000, 100, N'Tập yoga tại nhà'),
    (70, N'Tiếng Hàn TOPIC 3', N'Bùi Văn AA', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2023, 92000, 75, N'Tiếng Hàn trung cấp'),
    (71, N'Lập trình Microservices', N'Hoàng Thị AB', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 178000, 50, N'Kiến trúc Microservices'),
    (72, N'Quản trị rủi ro', N'Ngô Văn AC', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 115000, 65, N'Quản lý rủi ro tài chính'),
    (73, N'Vẽ tranh cơ bản', N'Phạm Thị AD', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2022, 75000, 90, N'Học vẽ từ cơ bản'),
    (74, N'Du lịch Việt Nam', N'Trần Văn AE', N'Nhà xuất bản Văn hóa', N'Đời sống', 2023, 95000, 110, N'Địa điểm du lịch hấp dẫn'),
    (75, N'Lập trình AI', N'Lê Thị AF', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 195000, 40, N'Trí tuệ nhân tạo cơ bản'),
    (76, N'Tiếng Đức B1', N'Vũ Văn AG', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2023, 98000, 60, N'Tiếng Đức sơ trung'),
    (77, N'Tâm lý trẻ em', N'Đặng Thị AH', N'Nhà xuất bản Văn hóa', N'Khoa học xã hội', 2023, 88000, 80, N'Tâm lý học trẻ em'),
    (78, N'Thời trang hiện đại', N'Bùi Văn AI', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2024, 115000, 55, N'Xu hướng thời trang'),
    (79, N'Gym và Fitness', N'Hoàng Thị AJ', N'Nhà xuất bản Thể thao', N'Sức khỏe', 2023, 92000, 125, N'Tập gym tại nhà'),
    (80, N'Lập trình Blockchain', N'Ngô Văn AK', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 185000, 35, N'Công nghệ Blockchain'),
    (81, N'Tiếng Pháp B1', N'Phạm Thị AL', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2023, 85000, 70, N'Tiếng Pháp trung cấp'),
    (82, N'Luật kinh doanh', N'Trần Văn AM', N'Nhà xuất bản Pháp luật', N'Pháp luật', 2023, 125000, 60, N'Pháp luật doanh nghiệp'),
    (83, N'Nấu ăn chay', N'Lê Thị AN', N'Nhà xuất bản Ẩm thực', N'Ẩm thực', 2024, 78000, 95, N'Món chay ngon lành'),
    (84, N'Lập trình DevOps', N'Vũ Văn AO', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 168000, 45, N'DevOps và CI/CD'),
    (85, N'Tiếng Nhật N2', N'Đặng Thị AP', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2024, 105000, 65, N'Tiếng Nhật cao cấp'),
    (86, N'Khởi nghiệp', N'Bùi Văn AQ', N'Nhà xuất bản Tài chính', N'Kinh tế', 2024, 98000, 150, N'Khởi đầu doanh nghiệp'),
    (87, N'Vẽ tranh phong cảnh', N'Hoàng Thị AR', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2023, 95000, 75, N'Vẽ cảnh quan thiên nhiên'),
    (88, N'Massage trị liệu', N'Ngô Văn AS', N'Nhà xuất bản Thể thao', N'Sức khỏe', 2023, 82000, 90, N'Kỹ thuật massage'),
    (89, N'Lập trình Cloud', N'Phạm Thị AT', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 175000, 40, N'Điện toán đám mây'),
    (90, N'Tiếng Trung HSK 5', N'Trần Văn AU', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2024, 98000, 55, N'Tiếng Trung cao cấp'),
    (91, N'Kế toán thuế', N'Lê Thị AV', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 115000, 70, N'Kế toán thuế doanh nghiệp'),
    (92, N'Nhiếp ảnh du lịch', N'Vũ Văn AW', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2024, 135000, 50, N'Chụp ảnh khi đi du lịch'),
    (93, N'Chăm sóc sức khỏe', N'Đặng Thị AX', N'Nhà xuất bản Thể thao', N'Sức khỏe', 2023, 92000, 110, N'Health and wellness'),
    (94, N'Lập trình Data Science', N'Bùi Văn AY', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 188000, 35, N'Khoa học dữ liệu'),
    (95, N'Tiếng Hàn TOPIC 4', N'Hoàng Thị AZ', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2024, 98000, 60, N'Tiếng Hàn cao cấp'),
    (96, N'Quản lý tài chính', N'Ngô Văn BA', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 145000, 55, N'Quản lý tài chính cá nhân'),
    (97, N'Thiết kế nội thất', N'Phạm Thị BB', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2024, 175000, 40, N'Thiết kế không gian sống'),
    (98, N'Ẩm thực Pháp', N'Trần Văn BC', N'Nhà xuất bản Ẩm thực', N'Ẩm thực', 2024, 125000, 65, N'Món ăn Pháp'),
    (99, N'Lập trình Security', N'Lê Thị BD', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 165000, 45, N'An ninh mạng'),
    (100, N'Tiếng Nhật N1', N'Vũ Văn BE', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2024, 115000, 50, N'Tiếng Nhật cao cấp nhất'),
    (101, N'Bất động sản', N'Đặng Thị BF', N'Nhà xuất bản Tài chính', N'Kinh tế', 2024, 135000, 75, N'Đầu tư bất động sản'),
    (102, N'Múa cổ điển', N'Bùi Văn BG', N'Nhà xuất bản Văn hóa', N'Nghệ thuật', 2023, 88000, 60, N'Múa ba lê cơ bản'),
    (103, N'Chạy bộ marathon', N'Hoàng Thị BH', N'Nhà xuất bản Thể thao', N'Thể thao', 2024, 75000, 95, N'Chạy bộ đường dài'),
    (104, N'Lập trình IoT', N'Ngô Văn BI', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 155000, 40, N'Internet of Things'),
    (105, N'Tiếng Trung HSK 6', N'Phạm Thị BJ', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2024, 105000, 45, N'Tiếng Trung thượng cấp'),
    (106, N'Kinh tế chia sẻ', N'Trần Văn BK', N'Nhà xuất bản Tài chính', N'Kinh tế', 2024, 98000, 85, N'Mô hình kinh tế mới'),
    (107, N'Nhiếp ảnh chân dung', N'Lê Thị BL', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2024, 145000, 50, N'Chụp ảnh người'),
    (108, N'Yoga trị bệnh', N'Vũ Văn BM', N'Nhà xuất bản Thể thao', N'Sức khỏe', 2023, 88000, 80, N'Yoga chữa lành'),
    (109, N'Lập trình AR/VR', N'Đặng Thị BN', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 195000, 30, N'Thực tế ảo'),
    (110, N'Tiếng Hàn TOPIC 5', N'Bùi Văn BO', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2024, 105000, 50, N'Tiếng Hàn cao cấp'),
    (111, N'Thương mại điện tử', N'Hoàng Thị BP', N'Nhà xuất bản Tài chính', N'Kinh tế', 2024, 118000, 95, N'E-commerce business'),
    (112, N'Điêu khắc cơ bản', N'Ngô Văn BQ', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2023, 125000, 45, N'Học điêu khắc'),
    (113, N'Dinh dưỡng thể thao', N'Phạm Thị BR', N'Nhà xuất bản Thể thao', N'Sức khỏe', 2024, 92000, 85, N'Nutrition for athletes'),
    (114, N'Lập trình Quantum', N'Trần Văn BS', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 215000, 25, N'Điện toán lượng tử'),
    (115, N'Tiếng Đức B2', N'Lê Thị BT', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2024, 105000, 55, N'Tiếng Đức trung cấp'),
    (116, N'Fintech', N'Vũ Văn BU', N'Nhà xuất bản Tài chính', N'Kinh tế', 2024, 145000, 60, N'Công nghệ tài chính'),
    (117, N'Thiết kế thời trang', N'Đặng Thị BV', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2024, 165000, 40, N'Thiết kế trang phục'),
    (118, N'Ẩm thực Nhật Bản', N'Bùi Văn BW', N'Nhà xuất bản Ẩm thực', N'Ẩm thực', 2024, 135000, 70, N'Món ăn Nhật Bản'),
    (119, N'Lập trình Game', N'Hoàng Thị BX', N'Nhà xuất bản Khoa học', N'Công nghệ', 2024, 175000, 50, N'Phát triển game'),
    (120, N'Quản trị chuỗi cung ứng', N'Ngô Văn BY', N'Nhà xuất bản Tài chính', N'Kinh tế', 2024, 128000, 65, N'Supply chain management');
SET IDENTITY_INSERT Sach OFF;
GO

-- ============================================
-- BỔ SUNG HÓA ĐƠN (thêm 100 hóa đơn - tổng 155 hóa đơn)
-- ============================================
INSERT INTO HoaDon (MaHoaDonString, MaKhachHang, MaNhanVien, NgayLap, TongTien, GiamGia, ThanhToan, PhuongThucThanhToan, GhiChu, TrangThai)
VALUES
    ('HD00056', 21, 3, '2024-02-29', 289000, 0, 289000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00057', 22, 5, '2024-03-01', 356000, 15000, 341000, N'Chuyển khoản', N'Giảm giá 15k', N'Đã thanh toán'),
    ('HD00058', 23, 2, '2024-03-02', 467000, 0, 467000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00059', 24, 8, '2024-03-03', 198000, 5000, 193000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00060', 25, 3, '2024-03-04', 534000, 20000, 514000, N'Chuyển khoản', N'Giảm giá 20k', N'Đã thanh toán'),
    ('HD00061', 26, 5, '2024-03-05', 245000, 0, 245000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00062', 27, 2, '2024-03-06', 378000, 0, 378000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00063', 28, 7, '2024-03-07', 189000, 0, 189000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00064', 29, 3, '2024-03-08', 456000, 25000, 431000, N'Tiền mặt', N'Giảm giá 25k', N'Đã thanh toán'),
    ('HD00065', 30, 5, '2024-03-09', 312000, 0, 312000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00066', 31, 2, '2024-03-10', 167000, 0, 167000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00067', 32, 8, '2024-03-11', 289000, 10000, 279000, N'Chuyển khoản', N'Giảm giá 10k', N'Đã thanh toán'),
    ('HD00068', 33, 3, '2024-03-12', 445000, 0, 445000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00069', 34, 5, '2024-03-13', 234000, 0, 234000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00070', 35, 2, '2024-03-14', 398000, 15000, 383000, N'Tiền mặt', N'Giảm giá 15k', N'Đã thanh toán'),
    ('HD00071', 36, 7, '2024-03-15', 267000, 0, 267000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00072', 37, 3, '2024-03-16', 512000, 0, 512000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00073', 38, 5, '2024-03-17', 178000, 0, 178000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00074', 39, 2, '2024-03-18', 356000, 20000, 336000, N'Tiền mặt', N'Giảm giá 20k', N'Đã thanh toán'),
    ('HD00075', 40, 8, '2024-03-19', 489000, 0, 489000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00076', 41, 3, '2024-03-20', 198000, 0, 198000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00077', 42, 5, '2024-03-21', 267000, 5000, 262000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00078', 43, 2, '2024-03-22', 445000, 0, 445000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00079', 44, 7, '2024-03-23', 378000, 15000, 363000, N'Tiền mặt', N'Giảm giá 15k', N'Đã thanh toán'),
    ('HD00080', 45, 3, '2024-03-24', 189000, 0, 189000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00081', 46, 5, '2024-03-25', 534000, 0, 534000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00082', 47, 2, '2024-03-26', 312000, 10000, 302000, N'Chuyển khoản', N'Giảm giá 10k', N'Đã thanh toán'),
    ('HD00083', 48, 8, '2024-03-27', 167000, 0, 167000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00084', 49, 3, '2024-03-28', 456000, 0, 456000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00085', 50, 5, '2024-03-29', 289000, 0, 289000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00086', 51, 2, '2024-03-30', 398000, 20000, 378000, N'Tiền mặt', N'Giảm giá 20k', N'Đã thanh toán'),
    ('HD00087', 52, 7, '2024-03-31', 234000, 0, 234000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00088', 53, 3, '2024-04-01', 512000, 15000, 497000, N'Chuyển khoản', N'Giảm giá 15k', N'Đã thanh toán'),
    ('HD00089', 54, 5, '2024-04-02', 178000, 0, 178000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00090', 55, 2, '2024-04-03', 356000, 0, 356000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00091', 56, 8, '2024-04-04', 489000, 0, 489000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00092', 57, 3, '2024-04-05', 198000, 5000, 193000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00093', 58, 5, '2024-04-06', 267000, 0, 267000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00094', 59, 2, '2024-04-07', 445000, 25000, 420000, N'Tiền mặt', N'Giảm giá 25k', N'Đã thanh toán'),
    ('HD00095', 60, 7, '2024-04-08', 378000, 0, 378000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00096', 61, 3, '2024-04-09', 189000, 0, 189000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00097', 62, 5, '2024-04-10', 534000, 0, 534000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00098', 63, 2, '2024-04-11', 312000, 10000, 302000, N'Chuyển khoản', N'Giảm giá 10k', N'Đã thanh toán'),
    ('HD00099', 64, 8, '2024-04-12', 167000, 0, 167000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00100', 65, 3, '2024-04-13', 456000, 0, 456000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00101', 66, 5, '2024-04-14', 289000, 15000, 274000, N'Chuyển khoản', N'Giảm giá 15k', N'Đã thanh toán'),
    ('HD00102', 67, 2, '2024-04-15', 398000, 0, 398000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00103', 68, 7, '2024-04-16', 234000, 0, 234000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00104', 69, 3, '2024-04-17', 512000, 0, 512000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00105', 70, 5, '2024-04-18', 178000, 5000, 173000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00106', 71, 2, '2024-04-19', 356000, 0, 356000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00107', 72, 8, '2024-04-20', 489000, 20000, 469000, N'Chuyển khoản', N'Giảm giá 20k', N'Đã thanh toán'),
    ('HD00108', 73, 3, '2024-04-21', 198000, 0, 198000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00109', 74, 5, '2024-04-22', 267000, 0, 267000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00110', 75, 2, '2024-04-23', 445000, 0, 445000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00111', 76, 7, '2024-04-24', 378000, 15000, 363000, N'Tiền mặt', N'Giảm giá 15k', N'Đã thanh toán'),
    ('HD00112', 77, 3, '2024-04-25', 189000, 0, 189000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00113', 78, 5, '2024-04-26', 534000, 0, 534000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00114', 79, 2, '2024-04-27', 312000, 0, 312000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00115', 80, 8, '2024-04-28', 167000, 0, 167000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00116', 81, 3, '2024-04-29', 456000, 0, 456000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00117', 82, 5, '2024-04-30', 289000, 0, 289000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00118', 83, 2, '2024-05-01', 398000, 25000, 373000, N'Chuyển khoản', N'Giảm giá 25k', N'Đã thanh toán'),
    ('HD00119', 84, 7, '2024-05-02', 234000, 0, 234000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00120', 85, 3, '2024-05-03', 512000, 0, 512000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00121', 86, 5, '2024-05-04', 178000, 0, 178000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00122', 87, 2, '2024-05-05', 356000, 0, 356000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00123', 88, 8, '2024-05-06', 489000, 10000, 479000, N'Chuyển khoản', N'Giảm giá 10k', N'Đã thanh toán'),
    ('HD00124', 89, 3, '2024-05-07', 198000, 0, 198000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00125', 90, 5, '2024-05-08', 267000, 0, 267000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00126', 91, 2, '2024-05-09', 445000, 0, 445000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00127', 92, 7, '2024-05-10', 378000, 0, 378000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00128', 93, 3, '2024-05-11', 189000, 5000, 184000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00129', 94, 5, '2024-05-12', 534000, 0, 534000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00130', 95, 2, '2024-05-13', 312000, 0, 312000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00131', 96, 8, '2024-05-14', 167000, 0, 167000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00132', 97, 3, '2024-05-15', 456000, 15000, 441000, N'Tiền mặt', N'Giảm giá 15k', N'Đã thanh toán'),
    ('HD00133', 98, 5, '2024-05-16', 289000, 0, 289000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00134', 99, 2, '2024-05-17', 398000, 0, 398000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00135', 100, 7, '2024-05-18', 234000, 0, 234000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00136', 1, 3, '2024-05-19', 512000, 0, 512000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00137', 2, 5, '2024-05-20', 178000, 0, 178000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00138', 3, 2, '2024-05-21', 356000, 20000, 336000, N'Tiền mặt', N'Giảm giá 20k', N'Đã thanh toán'),
    ('HD00139', 4, 8, '2024-05-22', 489000, 0, 489000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00140', 5, 3, '2024-05-23', 198000, 0, 198000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00141', 6, 5, '2024-05-24', 267000, 0, 267000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00142', 7, 2, '2024-05-25', 445000, 0, 445000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00143', 8, 7, '2024-05-26', 378000, 15000, 363000, N'Chuyển khoản', N'Giảm giá 15k', N'Đã thanh toán'),
    ('HD00144', 9, 3, '2024-05-27', 189000, 0, 189000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00145', 10, 5, '2024-05-28', 534000, 0, 534000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00146', 11, 2, '2024-05-29', 312000, 0, 312000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00147', 12, 8, '2024-05-30', 167000, 0, 167000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00148', 13, 3, '2024-05-31', 456000, 0, 456000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00149', 14, 5, '2024-06-01', 289000, 0, 289000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00150', 15, 2, '2024-06-02', 398000, 25000, 373000, N'Tiền mặt', N'Giảm giá 25k', N'Đã thanh toán'),
    ('HD00151', 16, 7, '2024-06-03', 234000, 0, 234000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00152', 17, 3, '2024-06-04', 512000, 0, 512000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00153', 18, 5, '2024-06-05', 178000, 0, 178000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00154', 19, 2, '2024-06-06', 356000, 0, 356000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00155', 20, 8, '2024-06-07', 489000, 10000, 479000, N'Chuyển khoản', N'Giảm giá 10k', N'Đã thanh toán');
GO

-- ============================================
-- BỔ SUNG ĐƠN NHẬP HÀNG (thêm 25 đơn - tổng 50 đơn)
-- ============================================
INSERT INTO DonNhapHang (MaDonNhapString, MaNhaCungCap, MaNhanVien, NgayNhap, TongTien, GhiChu, TrangThai)
VALUES
    ('PN00026', 5, 4, '2024-03-12', 9200000, N'Nhập sách tháng 3 lần 2', N'Đã nhập'),
    ('PN00027', 6, 4, '2024-03-15', 7500000, N'Nhập sách mỹ thuật lần 2', N'Đã nhập'),
    ('PN00028', 7, 4, '2024-03-18', 5800000, N'Nhập sách ẩm thực lần 2', N'Đã nhập'),
    ('PN00029', 8, 4, '2024-03-20', 11200000, N'Nhập sách khoa học lần 2', N'Đã nhập'),
    ('PN00030', 9, 4, '2024-03-22', 8200000, N'Nhập sách văn hóa lần 2', N'Đã nhập'),
    ('PN00031', 10, 4, '2024-03-25', 6400000, N'Nhập sách ngoại ngữ lần 2', N'Đã nhập'),
    ('PN00032', 1, 4, '2024-04-01', 9800000, N'Nhập sách tháng 4', N'Đã nhập'),
    ('PN00033', 2, 4, '2024-04-05', 8700000, N'Nhập sách giáo khoa lần 3', N'Đã nhập'),
    ('PN00034', 3, 4, '2024-04-08', 5600000, N'Nhập sách thiếu nhi lần 3', N'Đã nhập'),
    ('PN00035', 4, 4, '2024-04-10', 13800000, N'Nhập sách kinh tế lần 3', N'Đã nhập'),
    ('PN00036', 5, 4, '2024-04-12', 10500000, N'Nhập sách đại học lần 3', N'Đã nhập'),
    ('PN00037', 6, 4, '2024-04-15', 7200000, N'Nhập sách mỹ thuật lần 3', N'Đã nhập'),
    ('PN00038', 7, 4, '2024-04-18', 5900000, N'Nhập sách ẩm thực lần 3', N'Đã nhập'),
    ('PN00039', 8, 4, '2024-04-20', 11800000, N'Nhập sách khoa học lần 3', N'Đã nhập'),
    ('PN00040', 9, 4, '2024-04-22', 8600000, N'Nhập sách văn hóa lần 3', N'Đã nhập'),
    ('PN00041', 10, 4, '2024-04-25', 6600000, N'Nhập sách ngoại ngữ lần 3', N'Đã nhập'),
    ('PN00042', 11, 4, '2024-04-28', 4800000, N'Nhập sách thể thao lần 2', N'Đã nhập'),
    ('PN00043', 12, 4, '2024-05-01', 4200000, N'Nhập sách pháp luật lần 2', N'Đã nhập'),
    ('PN00044', 1, 4, '2024-05-05', 10200000, N'Nhập sách tháng 5', N'Đã nhập'),
    ('PN00045', 2, 4, '2024-05-08', 9200000, N'Nhập sách giáo khoa lần 4', N'Đã nhập'),
    ('PN00046', 3, 4, '2024-05-10', 5900000, N'Nhập sách thiếu nhi lần 4', N'Đã nhập'),
    ('PN00047', 4, 4, '2024-05-12', 14500000, N'Nhập sách kinh tế lần 4', N'Đã nhập'),
    ('PN00048', 5, 4, '2024-05-15', 10800000, N'Nhập sách đại học lần 4', N'Đã nhập'),
    ('PN00049', 6, 4, '2024-05-18', 7600000, N'Nhập sách mỹ thuật lần 4', N'Đã nhập'),
    ('PN00050', 7, 4, '2024-05-20', 6100000, N'Nhập sách ẩm thực lần 4', N'Đã nhập');
GO

-- ============================================
-- BỔ SUNG CHI TIẾT ĐƠN NHẬP (từ đơn 26-50)
-- ============================================
INSERT INTO ChiTietDonNhap (MaDonNhap, MaSach, SoLuong, DonGia, ThanhTien)
VALUES
    (26, 2, 80, 52000, 4160000),
    (26, 3, 40, 85000, 3400000),
    (26, 9, 35, 65000, 2275000),
    (27, 7, 30, 120000, 3600000),
    (27, 15, 25, 125000, 3125000),
    (27, 22, 10, 900000, 900000),
    (28, 8, 80, 50000, 4000000),
    (28, 16, 30, 55000, 1650000),
    (29, 24, 70, 95000, 6650000),
    (29, 32, 35, 120000, 4200000),
    (29, 37, 15, 92000, 1380000),
    (30, 21, 65, 70000, 4550000),
    (30, 46, 50, 65000, 3250000),
    (30, 54, 20, 50000, 1000000),
    (31, 3, 70, 45000, 3150000),
    (31, 10, 40, 58000, 2320000),
    (31, 18, 30, 52000, 1560000),
    (32, 1, 90, 60000, 5400000),
    (32, 2, 60, 52000, 3120000),
    (33, 4, 60, 85000, 5100000),
    (33, 11, 30, 90000, 2700000),
    (33, 38, 25, 80000, 2000000),
    (34, 5, 55, 65000, 3575000),
    (34, 54, 40, 50000, 2000000),
    (35, 6, 85, 100000, 8500000),
    (35, 20, 30, 115000, 3450000),
    (35, 14, 20, 80000, 1600000),
    (36, 19, 80, 75000, 6000000),
    (36, 25, 35, 85000, 2975000),
    (36, 40, 45, 50000, 2250000),
    (37, 7, 35, 120000, 4200000),
    (37, 22, 30, 90000, 2700000),
    (38, 8, 75, 50000, 3750000),
    (38, 16, 35, 55000, 1925000),
    (39, 24, 65, 95000, 6175000),
    (39, 32, 40, 120000, 4800000),
    (39, 43, 20, 95000, 1900000),
    (40, 21, 55, 70000, 3850000),
    (40, 46, 45, 65000, 2925000),
    (40, 51, 50, 42000, 2100000),
    (41, 3, 60, 45000, 2700000),
    (41, 10, 35, 58000, 2030000),
    (41, 18, 35, 52000, 1820000),
    (42, 27, 50, 50000, 2500000),
    (42, 35, 30, 60000, 1800000),
    (42, 49, 20, 60000, 1200000),
    (43, 29, 60, 45000, 2700000),
    (43, 33, 40, 30000, 1200000),
    (44, 1, 85, 60000, 5100000),
    (44, 2, 65, 52000, 3380000),
    (45, 4, 55, 85000, 4675000),
    (45, 11, 30, 90000, 2700000),
    (46, 5, 50, 65000, 3250000),
    (46, 54, 45, 50000, 2250000),
    (47, 6, 80, 100000, 8000000),
    (47, 20, 35, 115000, 4025000),
    (47, 26, 30, 110000, 3300000),
    (48, 19, 70, 75000, 5250000),
    (48, 25, 40, 85000, 3400000),
    (48, 40, 50, 50000, 2500000),
    (49, 7, 40, 120000, 4800000),
    (49, 22, 25, 90000, 2250000),
    (50, 8, 70, 50000, 3500000),
    (50, 16, 40, 55000, 2200000);
GO

-- ============================================
-- BỔ SUNG TÀI KHOẢN (thêm tài khoản)
-- ============================================
SET IDENTITY_INSERT TaiKhoan ON;
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, VaiTro, MaNhanVien, TrangThai)
VALUES
    (5, N'nv004', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 5, N'Hoạt động'),
    (6, N'nv005', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 6, N'Hoạt động'),
    (7, N'nv006', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 7, N'Hoạt động'),
    (8, N'nv007', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 8, N'Hoạt động'),
    (9, N'nv008', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 9, N'Hoạt động'),
    (10, N'nv009', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 10, N'Hoạt động'),
    (11, N'nv010', N'e10adc3949ba59abbe56e057f20f883e', N'Thu ngân', 11, N'Hoạt động'),
    (12, N'nv011', N'e10adc3949ba59abbe56e057f20f883e', N'Thu ngân', 12, N'Hoạt động'),
    (13, N'nv012', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 13, N'Hoạt động'),
    (14, N'nv013', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 14, N'Hoạt động'),
    (15, N'nv014', N'e10adc3949ba59abbe56e057f20f883e', N'Thu ngân', 15, N'Hoạt động'),
    (16, N'nv015', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 16, N'Hoạt động'),
    (17, N'nv016', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 17, N'Hoạt động'),
    (18, N'nv017', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 18, N'Hoạt động'),
    (19, N'nv018', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 19, N'Hoạt động'),
    (20, N'nv019', N'e10adc3949ba59abbe56e057f20f883e', N'Quản lý kho', 20, N'Hoạt động');
SET IDENTITY_INSERT TaiKhoan OFF;
GO

PRINT N'Đã thêm dữ liệu mẫu thành công!';
GO
UPDATE TaiKhoan 
SET MatKhau = 'e10adc3949ba59abbe56e057f20f883e' 
WHERE MatKhau IN ('0192023a7bbd73250516f069df18b500', '202cb962ac59075b964b07152d234b70');
SELECT * FROM TaiKhoan;