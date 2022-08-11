#!/bin/bash
## 系统信息相关

source Profile.sh


# 检测sudoer状态 0:不是sudoer 1:需要密码的sudoer 2:无密码的sudoer 注意：需要root密码
# checkSudoer "$root passwd"
checkSudoer (){
  # 检查是否在sudo组中 0 false 1 true
  IS_SUDOER=-1
  is_sudoer=-1
  IS_SUDO_NOPASSWD=-1
  is_sudo_nopasswd=-1
  # 检查是否在sudo组
  if groups| grep sudo > /dev/null ;then
    # 是sudo组
    IS_SUDOER=1
    RETURN_VAR=1
    is_sudoer="TRUE"
    # 检查是否免密码sudo 括号得注释
    check_var="ALL=\(ALL\)NOPASSWD:ALL"
    if doAsRoot -p "$1" -c "cat '/etc/sudoers' | grep ^$CURRENT_USER | grep $check_var > /dev/null" ;then
        # sudo免密码
        IS_SUDO_NOPASSWD=1
        is_sudo_nopasswd="TRUE"
        RETURN_VAR=2
        return 2
    else
        # sudo要密码
        IS_SUDO_NOPASSWD=0
        is_sudo_nopasswd="FALSE"
        RETURN_VAR=1
        return 1
    fi
  else
    # 不是sudoer
    IS_SUDOER=0
    IS_SUDO_NOPASSWD=0
    is_sudoer="FALSE"
    RETURN_VAR=0
    is_sudo_nopasswd="No a sudoer"
    return 0
  fi
}

# 检查是否是GNOME
isGNOME (){
  # IS_GNOME_DE=-1
  check_var="gnome"
  if echo $DESKTOP_SESSION | grep $check_var > /dev/null ;then
    # IS_GNOME_DE="TRUE"
    prompt -s GNOME Detected: $DESKTOP_SESSION""
    RETURN_VAR=1
    return 1
  else
    # IS_GNOME_DE="FALSE"
    prompt -e "不是GNOME桌面环境($DESKTOP_SESSION)"
    RETURN_VAR=0
    return 0
  fi
}


# 检查是否有root权限
checkForRoot (){
  prompt -i "\n检查权限  ——    Checking for root access...\n"
  if [ "$UID" -eq 0 ]; then
    # Error message
    prompt -w "\n——————————  ROOT ACCESS  ——————————\n"
    # prompt -e "\n [ Error ] -> 请不要使用管理员权限运行 Please DO NOT run as root  \n"
    # exit 1
    RETURN_VAR=1
    return 1
  else
    prompt -i "\n——————————  Root not available  ——————————\n"
    RETURN_VAR=0
    return 0
  fi
}


# 检测是否为苹果系统
isMacOS (){
  if [ -e /usr/bin/uname ]; then
    if [ "`/usr/bin/uname -s`" = "Darwin" ]; then
        prompt -s '*** Detected MacOS / Darwin !'
        # prompt -e '*** Linux only!'
        RETURN_VAR=1
        return 1
    else 
        prompt -e '*** Not a MacOS / Darwin !'
        RETURN_VAR=0
        return 0
    fi
  fi
}


# 显示是否是Debian11，无返回值！
debianBullseyeDetect (){
  prompt -w '*** Detecting Linux Distribution....'
  if [ -f /etc/debian_version ]; then
    dvers=`cat /etc/debian_version | cut -d '.' -f 1 | cut -d '/' -f 1`
    if [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F -i LinuxMint`" ]; then
        # Linux Mint -> Ubuntu 'xenial'
        prompt -w '*** Found Linux Mint      Ubuntu xenial'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F trusty`" ]; then
        # Ubuntu 'trusty'
        prompt -w '*** Found Ubuntu      trusty'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F wily`" ]; then
        # Ubuntu 'wily'
        prompt -w '*** Found Ubuntu      wily'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F xenial`" ]; then
        # Ubuntu 'xenial'
        prompt -w '*** Found Ubuntu      xenial'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F zesty`" ]; then
        # Ubuntu 'zesty'
        prompt -w '*** Found Ubuntu      zesty'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F precise`" ]; then
        # Ubuntu 'precise'
        prompt -w '*** Found Ubuntu      recise'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F artful`" ]; then
        # Ubuntu 'artful'
        prompt -w '*** Found Ubuntu      artful'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F bionic`" ]; then
        # Ubuntu 'bionic'
        prompt -w '*** Found Ubuntu      bionic'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F yakkety`" ]; then
        # Ubuntu 'yakkety'
        prompt -w '*** Found Ubuntu      yakkety'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F disco`" ]; then
        # Ubuntu 'disco'
        prompt -w '*** Found Ubuntu      disco'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F focal`" ]; then
        # Ubuntu 'focal' -> Ubuntu 'bionic' (for now)
        prompt -w '*** Found Ubuntu      focal->bionic'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F hirsute`" ]; then
        # Ubuntu 'hirsute' -> Ubuntu 'bionic' (for now)
        prompt -w '*** Found Ubuntu      hirsute->bionic'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ -f /etc/lsb-release -a -n "`cat /etc/lsb-release 2>/dev/null | grep -F impish`" ]; then
        # Ubuntu 'impish' -> Ubuntu 'bionic' (for now)
        prompt -w '*** Found Ubuntu      impish->bionic'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ "$dvers" = "6" -o "$dvers" = "squeeze" ]; then
        # Debian 'squeeze'
        prompt -w '*** Found Debian      squeeze'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ "$dvers" = "7" -o "$dvers" = "wheezy" ]; then
        # Debian 'wheezy'
        prompt -w '*** Found Debian      wheezy'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ "$dvers" = "8" -o "$dvers" = "jessie" ]; then
        # Debian 'jessie'
        prompt -w '*** Found Debian      jessie'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ "$dvers" = "9" -o "$dvers" = "stretch" ]; then
        # Debian 'stretch'
        prompt -w '*** Found Debian      '
    elif [ "$dvers" = "10" -o "$dvers" = "buster" -o "$dvers" = "parrot" ]; then
        # Debian 'buster'
        prompt -w '*** Found Debian      buster'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    elif [ "$dvers" = "11" -o "$dvers" = "bullseye" ]; then
        # Debian 'bullseye'
        prompt -s '*** Found Debian      bullseye'
    elif [ "$dvers" = "testing" -o "$dvers" = "sid" -o "$dvers" = "bookworm" ]; then
        # Debian 'testing', 'sid', and 'bookworm' -> Debian 'bookworm'
        prompt -s '*** Found Debian      testing'
        prompt -w "*** WARN: Debian testing 或 sid，请谨慎使用！"
    else
        # Use Debian "buster" for unrecognized Debians
        prompt -w '*** Found Debian or Debian derivative      unrecognized Debians'
        prompt -w "*** WARN: 非本脚本指定的发行版，谨慎使用！ Not for this distribution, use with caution !"
    fi
  elif [ -f /etc/SuSE-release -o -f /etc/suse-release -o -f /etc/SUSE-brand -o -f /etc/SuSE-brand -o -f /etc/suse-brand ]; then
    prompt -e '*** Found SuSE, required zypper YUM repo...'
    prompt -e '*** Debian only !'
    exit 1
  elif [ -d /etc/yum.repos.d ]; then
    if [ -n "`cat /etc/redhat-release 2>/dev/null | grep -i fedora`" ]; then
        prompt -e "*** Found Fedora, required /etc/yum.repos.d/xxx.repo"
        prompt -e '*** Debian only !'
        exit 1
    elif [ -n "`cat /etc/redhat-release 2>/dev/null | grep -i centos`" -o -n "`cat /etc/redhat-release 2>/dev/null | grep -i enterprise`" ]; then
        prompt -e "*** Found RHEL/CentOS, required /etc/yum.repos.d/xxx.repo"
        prompt -e '*** Debian only !'
        exit 1
    elif [ -n "`cat /etc/system-release 2>/dev/null | grep -i amazon`" ]; then
        prompt -e "*** Found Amazon (CentOS/RHEL based), required /etc/yum.repos.d/xxx.repo"
        if [ -n "`cat /etc/system-release 2>/dev/null | grep -F 'Amazon Linux 2'`" ]; then
            prompt -e '*** Debian only, NOT redhat/el/7 !'
            exit 1
        else
            prompt -e '*** Debian only, NOT redhat/amzn1/2016.03 !'
            exit 1
        fi
    else
        prompt -e "*** Found unknown yum-based repo, using el/7"
        prompt -e '*** Debian only !'
        exit 1
    fi
  fi
}


# 新建文件夹 $1 注意权限！系统文件需要sudo无密码
addFolder () {
    if [ $# -ne 1 ];then
        prompt -e "addFolder () 只能有一个参数"
    fi
    if ! [ -d $1 ];then
        prompt -x "新建文件夹$1 "
        mkdir $1
    fi
    if ! [ -d $1 ];then
        prompt -x "(sudo)新建文件夹$1 "
        sudo mkdir $1
    fi
}

# 备份配置文件。系统文件需要sudo无密码.先检查是否有bak结尾的备份文件，没有则创建，有则另外覆盖一个newbak文件。$1 :文件名
backupFile () {
    if [ -f "$1" ];then
        # 如果有bak备份文件 ，生成newbak
        if [ -f "$1.bak" ];then
            # bak文件存在
            prompt -x "(sudo)正在备份 $1 文件到 $1.newbak (覆盖) "
            sudo cp $1 $1.newbak
        else
            # 没有bak文件，创建备份
            prompt -x "(sudo)正在备份 $1 文件到 $1.bak"
            sudo cp $1 $1.bak
        fi
    else
        # 如果不存在要备份的文件,不执行
        prompt -e "没有$1文件，不做备份"
    fi
} 


# 检查root密码是否正确checkRootPasswd $1
checkRootPasswd () {
# 下面不能有缩进！
su - root <<! >/dev/null 2>/dev/null
$1
pwd
!
# echo $?
if [ "$?" -ne 0 ] ;then
    prompt -e "Root 用户密码不正确！"
    exit 1
fi
}

# 以root身份运行
# doAsRoot -p root密码 -c 命令
doAsRoot () {
while [[ $# -gt 0 ]];do
  key=${1}
  case ${key} in
    -p|--passwd)
      ROOT_PASSWD=${2}
      shift 2
      ;;
    -c|--command)
      COMMAND=${2}
      shift 2
      ;;
    *)
      echo "Usage: ${0} [-p|--passwd] [-c|--command]" 1>&2
      exit 1 
      shift
      ;;
  esac
done
FIRST_DO_AS_ROOT=1
# 第一次运行需要询问root密码
if [ "$FIRST_DO_AS_ROOT" -eq 1 ];then
    if [ "$ROOT_PASSWD" == "" ];then
        prompt -w "未在脚本里定义root用户密码，请输入root用户密码: ROOT_PASSWD"
        read -r input
        ROOT_PASSWD=$input
    fi
    # 检查密码
    checkRootPasswd
    FIRST_DO_AS_ROOT=0
fi
# 下面不能有缩进！
su - root <<!>/dev/null 2>&1
$ROOT_PASSWD
echo " Exec $COMMAND as root"
$COMMAND
!
}
