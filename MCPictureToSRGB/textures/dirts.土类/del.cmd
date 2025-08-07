@echo off
setlocal enabledelayedexpansion

:: 设置目标目录
cd /d "C:\Your\Target\Directory"

:: 定义要保留的文件列表
set "keepList=rename.cmd del.cmd dirt.png dirt.泥土.png grass_block.png grass_block.草方块.png coarse_dirt.png coarse_dirt.砂土.png rooted_dirt.png rooted_dirt.缠根泥土.png mud.png mud.泥巴.png clay.png clay.黏土块.png soul_sand.png soul_sand.灵魂沙.png soul_soil.png soul_soil.灵魂土.png"

:: 遍历当前目录下所有文件
for %%f in (*) do (
    set "found=false"
    for %%k in (!keepList!) do (
        if /I "%%f"=="%%k" set "found=true"
    )
    if "!found!"=="false" (
        echo 删除文件：%%f
        del /f /q "%%f"
    )
)

endlocal
