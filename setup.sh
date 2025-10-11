#!/bin/sh

set -e

echo "=================================="
echo "Shop Agent Installation Script"
echo "=================================="
echo ""

# 설치 디렉토리 설정
INSTALL_DIR="$HOME/mudda"
BINARY_NAME="shop-agent"
DOWNLOAD_URL="https://api.mudda.co.kr/download/shop-agent-linux-amd64"

# 색상 코드
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

printf "${YELLOW}Step 1/5:${NC} Checking system requirements...\n"

# OS 확인
OS_TYPE=$(uname -s)
case "$OS_TYPE" in
  Linux*)
    printf "${GREEN}✓${NC} Linux system detected\n"
    ;;
  Darwin*)
    printf "${RED}Error: This script is for Linux only. Detected OS: macOS${NC}\n"
    echo "Please download the macOS version:"
    echo "  https://api.mudda.co.kr/download/shop-agent-macos-arm64"
    exit 1
    ;;
  MINGW*|MSYS*|CYGWIN*)
    printf "${RED}Error: This script is for Linux only. Detected OS: Windows${NC}\n"
    echo "Please download the Windows version:"
    echo "  https://api.mudda.co.kr/download/shop-agent-windows-amd64.exe"
    exit 1
    ;;
  *)
    printf "${RED}Error: Unsupported OS: $OS_TYPE${NC}\n"
    echo "This script is for Linux only."
    exit 1
    ;;
esac
echo ""

# 설치 디렉토리 생성
printf "${YELLOW}Step 2/5:${NC} Creating installation directory...\n"
mkdir -p "$INSTALL_DIR"
printf "${GREEN}✓${NC} Directory created: $INSTALL_DIR\n"
echo ""

# 기존 프로세스 종료
printf "${YELLOW}Step 3/5:${NC} Checking for existing shop-agent process...\n"
# 실행 파일의 전체 경로로 더 정확하게 매칭
if pgrep -f "$INSTALL_DIR/$BINARY_NAME" > /dev/null 2>&1; then
    echo "Stopping existing shop-agent process..."
    pkill -f "$INSTALL_DIR/$BINARY_NAME" || true
    sleep 2
    printf "${GREEN}✓${NC} Existing process stopped\n"
else
    printf "${GREEN}✓${NC} No existing process found\n"
fi
echo ""

# 바이너리 다운로드
printf "${YELLOW}Step 4/5:${NC} Downloading shop-agent...\n"
echo "Downloading from: $DOWNLOAD_URL"

if command -v curl > /dev/null 2>&1; then
    curl -fsSL "$DOWNLOAD_URL" -o "$INSTALL_DIR/$BINARY_NAME"
elif command -v wget > /dev/null 2>&1; then
    wget -q "$DOWNLOAD_URL" -O "$INSTALL_DIR/$BINARY_NAME"
else
    printf "${RED}Error: Neither curl nor wget is available${NC}\n"
    exit 1
fi

printf "${GREEN}✓${NC} Download completed\n"
echo ""

# 실행 권한 부여
printf "${YELLOW}Step 5/5:${NC} Setting up permissions and starting service...\n"
chmod 755 "$INSTALL_DIR/$BINARY_NAME"
printf "${GREEN}✓${NC} Permissions set (755)\n"

# 백그라운드에서 실행
cd "$INSTALL_DIR"
nohup "./$BINARY_NAME" > shop-agent.log 2>&1 &
SHOP_AGENT_PID=$!

sleep 2

# 프로세스 확인
if ps -p $SHOP_AGENT_PID > /dev/null 2>&1; then
    printf "${GREEN}✓${NC} Shop-agent started successfully (PID: $SHOP_AGENT_PID)\n"
else
    printf "${RED}Error: Failed to start shop-agent${NC}\n"
    echo "Check the log file: $INSTALL_DIR/shop-agent.log"
    exit 1
fi

echo ""
echo "=================================="
printf "${GREEN}Installation completed!${NC}\n"
echo "=================================="
echo ""
echo "Installation directory: $INSTALL_DIR"
echo "Binary location: $INSTALL_DIR/$BINARY_NAME"
echo "Log file: $INSTALL_DIR/shop-agent.log"
echo "Process ID: $SHOP_AGENT_PID"
echo ""
echo "Useful commands:"
echo "  - View logs: tail -f $INSTALL_DIR/shop-agent.log"
echo "  - Stop service: pkill -f $BINARY_NAME"
echo "  - Check status: ps aux | grep $BINARY_NAME"
echo ""
echo "Shop-agent is now running in the background!"
echo ""