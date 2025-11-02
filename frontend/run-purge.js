import { PurgeCSS } from "purgecss";
import fs from "fs";
import path from "path";

// Chạy PurgeCSS
const purgeCSSResult = await new PurgeCSS().purge({
  content: ["./src/**/*.{html,js,jsx,ts,tsx}"],
  css: ["./src/styles/**/*.css"],
});

// Tạo thư mục output nếu chưa tồn tại
const outputDir = "./src/styles/clean_css";
if (!fs.existsSync(outputDir)) {
  fs.mkdirSync(outputDir, { recursive: true });
}

// Ghi từng file CSS sạch ra thư mục clean_css
purgeCSSResult.forEach((result) => {
  const fileName = path.basename(result.file);
  const outputPath = path.join(outputDir, fileName);
  fs.writeFileSync(outputPath, result.css, "utf8");
  console.log(`💾 Đã ghi file sạch: ${outputPath}`);
});

console.log("✅ PurgeCSS hoàn tất! File CSS sạch đã được lưu vào src/styles/clean_css/");
