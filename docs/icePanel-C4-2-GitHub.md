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


Component(4teQrlbMUj5LLqxdVcPK, "BAHPCA API", "")
Component(BxsQud7gKwTvBJQ0KNj2, "Commercial Analysis Export", "")


Rel(F2hUPV68esNdYOZlEDZp, 4teQrlbMUj5LLqxdVcPK, "Deploy")
Rel(F2hUPV68esNdYOZlEDZp, sevE1WSEK6p75cxmStkb, "Executes")
Rel(F2hUPV68esNdYOZlEDZp, BxsQud7gKwTvBJQ0KNj2, "Deploy")


  UpdateLayoutConfig($c4ShapeInRow="5", $c4BoundaryInRow="1")
