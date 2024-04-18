C4Context
  title System Context diagram for null

System_Boundary(axBWvtIPYmpByC3xkoHt, "GitHub") {
    Component(DzQ4VPgJYKxSkilz6snk, "BAHPCA_repo", "")
    Component(6uyz5GXgHFSVjrwIwRKC, "Common_Repo", "")
    Component(nd7uOWs4bfaeXm73vKFa, "Solr_Repo", "")
    Component(aik6DuJQc2KhSf4c5QZV, "CA Exporter", "")
    Component(F2hUPV68esNdYOZlEDZp, "Git Pipeline", "")
    Component(sevE1WSEK6p75cxmStkb, "Git Actions", "")
}


Component(OmsC5XSTv64h2ua9yNDK, "Solr API", "")
Container(Nr2rq32hBezwoAGeNT2U, "BAHPCA API Code", "", "")
Person(oTbmuU7ncIHqPnGupIDk, "Developer", "")
Container(Vg8nvjzPDirSBRjT8K3Q, "Solr API Code", "", "")
Component(4teQrlbMUj5LLqxdVcPK, "BAHPCA API", "")
Component(BxsQud7gKwTvBJQ0KNj2, "Commercial Analysis Export", "")
Container(DqaxYHtWGYqlWeYgZkyP, "Common Lib", "", "")


Rel(DzQ4VPgJYKxSkilz6snk, Nr2rq32hBezwoAGeNT2U, "Deployed as")
Rel(oTbmuU7ncIHqPnGupIDk, 6uyz5GXgHFSVjrwIwRKC, "Develops")
Rel(nd7uOWs4bfaeXm73vKFa, Vg8nvjzPDirSBRjT8K3Q, "Deployed as")
Rel(oTbmuU7ncIHqPnGupIDk, DzQ4VPgJYKxSkilz6snk, "Develops")
Rel(F2hUPV68esNdYOZlEDZp, 4teQrlbMUj5LLqxdVcPK, "Deploy")
Rel(oTbmuU7ncIHqPnGupIDk, nd7uOWs4bfaeXm73vKFa, "Develops")
Rel(DzQ4VPgJYKxSkilz6snk, F2hUPV68esNdYOZlEDZp, "Deployed via")
Rel(nd7uOWs4bfaeXm73vKFa, F2hUPV68esNdYOZlEDZp, "Deployed via")
Rel(aik6DuJQc2KhSf4c5QZV, F2hUPV68esNdYOZlEDZp, "Deployed via")
Rel(6uyz5GXgHFSVjrwIwRKC, DqaxYHtWGYqlWeYgZkyP, "Deployed as")
Rel(F2hUPV68esNdYOZlEDZp, OmsC5XSTv64h2ua9yNDK, "Deploy")
Rel(oTbmuU7ncIHqPnGupIDk, aik6DuJQc2KhSf4c5QZV, "Develops")
Rel(F2hUPV68esNdYOZlEDZp, sevE1WSEK6p75cxmStkb, "Executes")
Rel(F2hUPV68esNdYOZlEDZp, BxsQud7gKwTvBJQ0KNj2, "Deploy")


  UpdateLayoutConfig($c4ShapeInRow="5", $c4BoundaryInRow="1")
