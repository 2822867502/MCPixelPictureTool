@echo off
setlocal enabledelayedexpansion

:: ����Ŀ��Ŀ¼
cd /d "C:\Your\Target\Directory"

:: ����Ҫ�������ļ��б�
set "keepList=rename.cmd del.cmd dirt.png dirt.����.png grass_block.png grass_block.�ݷ���.png coarse_dirt.png coarse_dirt.ɰ��.png rooted_dirt.png rooted_dirt.��������.png mud.png mud.���.png clay.png clay.�����.png soul_sand.png soul_sand.���ɳ.png soul_soil.png soul_soil.�����.png"

:: ������ǰĿ¼�������ļ�
for %%f in (*) do (
    set "found=false"
    for %%k in (!keepList!) do (
        if /I "%%f"=="%%k" set "found=true"
    )
    if "!found!"=="false" (
        echo ɾ���ļ���%%f
        del /f /q "%%f"
    )
)

endlocal
