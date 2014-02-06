# !/bin/sh
# Please install ImageMagick to convert images !
# Laurent Constantin

convert star_on.png -resize 72x72 ../../res/drawable-hdpi/star_on.png
convert star_off.png -resize 72x72 ../../res/drawable-hdpi/star_off.png
	
convert star_on.png -resize 48x48 ../../res/drawable-mdpi/star_on.png
convert star_off.png -resize 48x48 ../../res/drawable-mdpi/star_off.png

convert star_on.png -resize 96x96 ../../res/drawable-xhdpi/star_on.png
convert star_off.png -resize 96x96 ../../res/drawable-xhdpi/star_off.png


convert as_615.png -resize 72x72 ../../res/drawable-hdpi/as.png
convert as_615.png -resize 384x384 ../../res/drawable-hdpi/as_large.png

convert as_615.png -resize 36x36 ../../res/drawable-ldpi/as.png
convert as_615.png -resize 192x192 ../../res/drawable-ldpi/as_large.png

convert as_615.png -resize 48x48 ../../res/drawable-mdpi/as.png
convert as_615.png -resize 256x256 ../../res/drawable-mdpi/as_large.png

convert as_615.png -resize 96x96 ../../res/drawable-xhdpi/as.png
convert as_615.png -resize 512x512 ../../res/drawable-xhdpi/as_large.png


convert SVG:as_main.svg -resize 150x150 ../../../jekyll/images/logo.png
convert SVG:as_main.svg -resize 128x128 ../../../jekyll/images/favicon.png