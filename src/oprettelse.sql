create table ProduktGruppe(
ting varchar,
produkt int

primary key(ting));


create table Produkt(
produktgruppe varchar,
navn varchar,
antalSalg int,
produktID int

primary key(produktID)
foreign key(produktgruppe) references ProduktGruppe);


create table Ordre(
ordreID int,
dato date,

primary key(ordreID));

create table OrdreIndhold(
indholdID int,
prisliste int,
pris int,
produkt int,
mængde int,
ordre int


primary key (indholdID)
foreign key(ordre) references Ordre);


create table prisListe(
navn varChar,
listeID int

primary key(listeID));




create table Pris(
pris money,
prisID int,
produkt int,
prisListe int

primary key(prisID)
foreign key(produkt) references produkt ,
foreign key(prisListe) references prisListe);



--insert into Produkt
--values('IPA', 0, 001);