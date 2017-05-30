for file in $(ls *.dot);
do
    dot -Tpdf $(basename $file .dot).dot >$(basename $file .dot).pdf
done
