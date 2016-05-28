Obrazloženje xml šema.

Propis:
	Odradila sam neke izmene što se tice strukture jer je na nekim mestima stajala sekvenca gde je trebalo da stoji izbor jer bi u suprotnom ispalo da možeš imati niz mešan od stavova i clanova npr.
	Sekvenca je strogo formatiran niz elemenata, nije skup, zato imamo choice. S obzirom da skoro svaki element od clana pa na dole može imati ili niz podelemenata ili direktno tekst.

Amandman:
	Predlog_propisa sadrži naziv propisa koj se menja (od onih koji su u proceduri).
	Jedan dokument može sadržati više amandmana na isti propis, zato je postavljeno na 1...inf
	Odredba je ništa drugo do selektor elementa koji se menja - ovaj amandman se odnosi na taj i taj deo propisa.
	Predlog_resenja sada ima tip u atributu da bi se znalo šta se radi sa tim. Za dodavanje treba da se dogovorimo da li ce biti append na referencirani element.
		Pored toga ima izbor izmedju elemenata - to ce biti novi sadržaj referenciranog elementa.
	Svaki amandman ima obrazloženje zašto je tu.