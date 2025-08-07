@echo off
setlocal enabledelayedexpansion

:: 设置目标目录
cd /d "C:\Your\Target\Directory"

:: 定义要保留的文件列表
set "keepList=rename.cmd del.cmd stone_bricks.png stone_bricks.石砖.png mossy_stone_bricks.png mossy_stone_bricks.苔石砖.png cracked_stone_bricks.png cracked_stone_bricks.裂纹石砖.png chiseled_stone_bricks.png chiseled_stone_bricks.錾制石砖.png deepslate_bricks.png deepslate_bricks.深板岩砖.png cracked_deepslate_bricks.png cracked_deepslate_bricks.裂纹深板岩砖.png deepslate_tiles.png deepslate_tiles.深板岩瓦.png cracked_deepslate_tiles.png cracked_deepslate_tiles.裂纹深板岩瓦.png chiseled_deepslate.png chiseled_deepslate.錾制深板岩.png polished_blackstone.png polished_blackstone.磨制黑石.png polished_blackstone_bricks.png polished_blackstone_bricks.磨制黑石砖.png cracked_polished_blackstone_bricks.png cracked_polished_blackstone_bricks.裂纹磨制黑石砖.png chiseled_polished_blackstone.png chiseled_polished_blackstone.錾制磨制黑石.png end_stone_bricks.png end_stone_bricks.末地石砖.png prismarine_bricks.png prismarine_bricks.海晶石砖.png dark_prismarine.png dark_prismarine.暗海晶石.png polished_deepslate.png polished_deepslate.磨制深板岩.png polished_diorite.png polished_diorite.磨制闪长岩.png polished_andesite.png polished_andesite.磨制安山岩.png polished_granite.png polished_granite.磨制花岗岩.png sandstone.png sandstone.砂岩.png chiseled_sandstone.png chiseled_sandstone.錾制砂岩.png cut_sandstone.png cut_sandstone.切制砂岩.png smooth_sandstone.png smooth_sandstone.平滑砂岩.png red_sandstone.png red_sandstone.红砂岩.png chiseled_red_sandstone.png chiseled_red_sandstone.錾制红砂岩.png cut_red_sandstone.png cut_red_sandstone.切制红砂岩.png smooth_red_sandstone.png smooth_red_sandstone.平滑红砂岩.png"

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
