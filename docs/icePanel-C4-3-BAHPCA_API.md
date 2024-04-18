C4Context
  title System Context diagram for null

Container_Boundary(4teQrlbMUj5LLqxdVcPK, "BAHPCA API", "") { 
    Container(DqaxYHtWGYqlWeYgZkyP, "Common Lib", "", "")
    Container(Nr2rq32hBezwoAGeNT2U, "BAHPCA API Code", "", "")
    Container(JOCj92Vc2CEAUN8hquBI, "Spring Framework", "", "")
}


ContainerDb(MzZVLTUQ74KbySVwdHfZ, "OpenSearch", "", "")
Container(Vg8nvjzPDirSBRjT8K3Q, "Solr API Code", "", "")
Component(DzQ4VPgJYKxSkilz6snk, "BAHPCA_repo", "")
Component(6uyz5GXgHFSVjrwIwRKC, "Common_Repo", "")


Rel(DzQ4VPgJYKxSkilz6snk, Nr2rq32hBezwoAGeNT2U, "Deployed as")
Rel(Vg8nvjzPDirSBRjT8K3Q, DqaxYHtWGYqlWeYgZkyP, "Uses")
Rel(MzZVLTUQ74KbySVwdHfZ, Nr2rq32hBezwoAGeNT2U, "reads data")
Rel(Vg8nvjzPDirSBRjT8K3Q, JOCj92Vc2CEAUN8hquBI, "Based on")
Rel(Nr2rq32hBezwoAGeNT2U, JOCj92Vc2CEAUN8hquBI, "Based on")
Rel(6uyz5GXgHFSVjrwIwRKC, DqaxYHtWGYqlWeYgZkyP, "Deployed as")


  UpdateLayoutConfig($c4ShapeInRow="5", $c4BoundaryInRow="1")
