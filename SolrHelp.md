# Solr help

https://lucene.apache.org/solr/guide/7_5/solr-control-script-reference.html#solr-control-script-reference

- start Solr
  - ./bin/solr start -e cloud
- stop all nodes
  - bin/solr stop -all
- restart node 1
  - ./bin/solr start -c -p 8983 -s example/cloud/node1/solr
- restart node 2 
  - ./bin/solr start -c -p 7574 -s example/cloud/node2/solr -z localhost:9983
  
  
- default admin url
  - http://localhost:8983/solr
- Solr indexing  
  - bin/post -c techproducts example/exampledocs/*
- simple select
  - curl "http://localhost:8983/solr/techproducts/select?indent=on&q=*:*"
  
- delete a collection 
  - bin/solr delete -c techproducts
    
- create a new collection:
  - bin/solr create -c <yourCollection> -s 2 -rf 2
  
- indexing binary files (example)
  - bin/post -c localMusicb  /media/syno/music-b/Texas/White\ on\ Blonde/*.flac

 bin/post -c localMusicb  /media/syno/music-b/**/*.flac

id:*.flac
xmpdm_album:"*Blonde" AND content_type:audio
style:"*Pop Rock" AND content_type:audio