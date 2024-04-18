C4Context
  title System Context diagram for null

Container_Boundary(OmsC5XSTv64h2ua9yNDK, "Solr API", "") { 
    Container(Vg8nvjzPDirSBRjT8K3Q, "Solr API Code", "", "")
}


Container(JOCj92Vc2CEAUN8hquBI, "Spring Framework", "", "")
ContainerDb(MzZVLTUQ74KbySVwdHfZ, "OpenSearch", "", "")
Component(heuHGHPQyPiOSCrreA29, "API Gateway", "")
Container(DqaxYHtWGYqlWeYgZkyP, "Common Lib", "", "")
Component(nd7uOWs4bfaeXm73vKFa, "Solr_Repo", "")


Rel(MzZVLTUQ74KbySVwdHfZ, Vg8nvjzPDirSBRjT8K3Q, "reads data")
Rel(Vg8nvjzPDirSBRjT8K3Q, DqaxYHtWGYqlWeYgZkyP, "Uses")
Rel(nd7uOWs4bfaeXm73vKFa, Vg8nvjzPDirSBRjT8K3Q, "Deployed as")
Rel(Vg8nvjzPDirSBRjT8K3Q, JOCj92Vc2CEAUN8hquBI, "Based on")
Rel(heuHGHPQyPiOSCrreA29, Vg8nvjzPDirSBRjT8K3Q, "Accesses API implementations")


  UpdateLayoutConfig($c4ShapeInRow="5", $c4BoundaryInRow="1")
