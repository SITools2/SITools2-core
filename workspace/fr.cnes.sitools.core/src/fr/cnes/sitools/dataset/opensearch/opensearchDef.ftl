<?xml version="1.0" encoding="UTF-8"?>
<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
  <ShortName>${name}</ShortName>
  <Url type="application/x-suggestions+json" template="${requestPath}/suggest?q={searchTerms}">
  </Url>
  <Url type="application/rss+xml" 
       method="get"
       template="${requestPath}/search?q={searchTerms}">
  </Url>
  <Url type="text/html" 
       method="get"
       template="${requestPath}/search?q={searchTerms}">
  </Url>
  <Image height="16" width="16" type="image/x-icon">${image.url!}</Image>
</OpenSearchDescription>
