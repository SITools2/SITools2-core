echo ""
echo "Building Mizar for Sitools"
node r.js -o buildMizarSitools.js
node r.js -o cssIn=../src/mizar/css/style.css out=../src/mizar/css/style.min.css

echo ""
echo "Done"