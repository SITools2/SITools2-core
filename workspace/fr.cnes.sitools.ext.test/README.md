# Healpix Filter

Activate SPHEROID spatial ref, execute from postgresql

    insert into spatial_ref_sys values(40000, 'ME', 1,'GEOGCS["Normal Sphere (r=6370997)",DATUM["unknown",SPHEROID["sphere",6370997,0]],PRIMEM["Greenwich",0],UNIT["degree",0.0174532925199433]]','+proj=longlat +ellps=sphere +no_defs');

Set the geometry column with srid as **40000**