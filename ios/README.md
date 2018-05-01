Co nefunguje
-----------------------------------------
* odesílání dat na server
    * server vrací error 500, máme špatně nastavené api
    * request by měl být v pořádku (nelze ověřit do odevzdání)
    * odesílání obrázku je volitelné, ale závisí na response z odeslání dat
* po spuštění imagePicker se v den odevzdání začala objevovat černá obrazovka
    * navození: při vytváření nového reportu se přidá fotografie přes imageView, akceptují se práva, korektně se otevře imagePicker, vybere se obrázek, okno se zavře a obrázek se přidá. Od této chvíle bude opakování tohoto postupu vracet nahodile černou obrazovku ve dvou situacích: 1.) Hned po otevření imagePicker, 2.) Po otevření imagePicker zobrazí menu PhotoLibrery a po kliknutí na Moments/Camera Roll, se objeví černá obrazovka. Stává se i při změně obrázku při vytváření reportu
    * občas po několika minutách se pro jeden výběr zprovozní a pak opět černá obrazovka
    * projel jsem několik vláken s doporučeními, ani jedno problém nevyřešilo. Podle komentářů má tento problém dost programátorů
    * chyba se projevuje na vícero strojích a vícero virtuálních verzí iPhonu
    * stejná chyba se projevila i na řešeném projektu ze cvik
* imagePicker (ukončení pomocí pick/cancel) vyvolává tuto hlášku: 2018-04-29 17:31:26.227483+0200 topgis-reporting[42138:1602229] [discovery] errors encountered while discovering extensions: Error Domain=PlugInKit Code=13 "query cancelled" UserInfo={NSLocalizedDescription=query cancelled}
    * nejedná se o error, jde jen o obyčejný výpis (asi warning)
    * vyzkoušel jsem pár cest popsaných na stackoverflow i jinde, žádná problém neodstranila (nezkoušel jsem upravit schéma jak někteří doporučovali)


Co funguje i když se odeslání nedaří (a nejde bez toho ověřit)
-----------------------------------------
* aktualizace záznamu v core data proběhne v pořádku
* setování cookie je v pořádku (potvrzuje odhlašování)
* nic jiného není na odeslání dat závislé
