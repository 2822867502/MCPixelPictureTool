@echo off
setlocal enabledelayedexpansion

:: ����Ŀ��Ŀ¼
cd /d "C:\Your\Target\Directory"

:: ����Ҫ�������ļ��б�
set "keepList=rename.cmd del.cmd stone_bricks.png stone_bricks.ʯש.png mossy_stone_bricks.png mossy_stone_bricks.̦ʯש.png cracked_stone_bricks.png cracked_stone_bricks.����ʯש.png chiseled_stone_bricks.png chiseled_stone_bricks.����ʯש.png deepslate_bricks.png deepslate_bricks.�����ש.png cracked_deepslate_bricks.png cracked_deepslate_bricks.���������ש.png deepslate_tiles.png deepslate_tiles.�������.png cracked_deepslate_tiles.png cracked_deepslate_tiles.�����������.png chiseled_deepslate.png chiseled_deepslate.���������.png polished_blackstone.png polished_blackstone.ĥ�ƺ�ʯ.png polished_blackstone_bricks.png polished_blackstone_bricks.ĥ�ƺ�ʯש.png cracked_polished_blackstone_bricks.png cracked_polished_blackstone_bricks.����ĥ�ƺ�ʯש.png chiseled_polished_blackstone.png chiseled_polished_blackstone.����ĥ�ƺ�ʯ.png end_stone_bricks.png end_stone_bricks.ĩ��ʯש.png prismarine_bricks.png prismarine_bricks.����ʯש.png dark_prismarine.png dark_prismarine.������ʯ.png polished_deepslate.png polished_deepslate.ĥ�������.png polished_diorite.png polished_diorite.ĥ��������.png polished_andesite.png polished_andesite.ĥ�ư�ɽ��.png polished_granite.png polished_granite.ĥ�ƻ�����.png sandstone.png sandstone.ɰ��.png chiseled_sandstone.png chiseled_sandstone.����ɰ��.png cut_sandstone.png cut_sandstone.����ɰ��.png smooth_sandstone.png smooth_sandstone.ƽ��ɰ��.png red_sandstone.png red_sandstone.��ɰ��.png chiseled_red_sandstone.png chiseled_red_sandstone.���ƺ�ɰ��.png cut_red_sandstone.png cut_red_sandstone.���ƺ�ɰ��.png smooth_red_sandstone.png smooth_red_sandstone.ƽ����ɰ��.png"

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
