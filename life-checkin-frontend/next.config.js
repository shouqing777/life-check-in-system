/** @type {import('next').NextConfig} */
const nextConfig = {
  eslint: {
    // 在構建過程中忽略 ESLint 錯誤
    ignoreDuringBuilds: true,
  },
};

module.exports = nextConfig;