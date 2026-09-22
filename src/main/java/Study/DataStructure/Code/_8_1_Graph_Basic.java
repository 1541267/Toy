package Study.DataStructure.Code;

/*
  - Graph ( u = 시작 정점(source vertex), v = 도착하는 정점(desination vertex)

  - 필요한 이유
    -> 트리는 특스한 형태의 그래프(사이클 X, Root O, 부모-자식 관계 명확)
    -> 현실 문제들 중엔 이런 계층 구조로는 표현이 안 되는 관계가 많음
    --> ex) 다대다 연결, 사이클 허용
    --> ex) 도로망(교차로 <-> 교차로), SNS 친구관계(양방향, 사이클 가능) 지하철 노선도, 의존성 그래프(A가 B 를 참조 동시에 B가 A를 참조)

    -> Vertex(정점): 그래프를 이루는 하나의 데이터 단위
      --> 트리의 Node와 비슷하지만 그래프에선 부모-자식 같은 위계가 없어 Vertex로 칭함
  
    -> Edge(간선): 두 Vertex 사이의 연결 (u, v) 
      --> 무방향(Undirected): (u, v) = (v = u) | 양쪽 다 서로를 알고 있음
      --> 방향(Directed): (u, v) != (v, u) | u -> v만 성립, u가 v를 가르킴
      --> 가중치(Weighted): (u ,v, weight) | 연결 비용/거리가 있음 (도로 길이, 항공료 등)
   
    -> Adjacent(인접): 두 Vertex가 Edge로 직접 연결되어 있으면 인접한다(Adjacent)고 함
      --> 직접 연결만 인접이라는 것, A-B-C 에서 A와 C는 인접이 아니라 연결(Connected)
   
    -> Degree(차수): 한 Vertex에 연결 된 Edge의 개수
      --> 무방향 그래프: Degree = 1
      --> 방향 그래프: in-degree(들어오는 간선 수) + out-degree(나가는 간선 수)로 분리
        ---> ex) 웹 페이지 그래프에서 in-degree가 높다 = 다른 페이지들이 나를 만이 링크(검색엔진 랭킹의 기본 아이디어와 연결)
    
    -> Path(경로): 한 Vertex에서 다른 Vertex까지 Eges를 따라 연속으로 이동하는 Vertex들의 나열
      --> A - B - C - D 처럼 인접한 쌍이 모두 Edge로 연결되어 있어야 함
      --> 경로의 길이(Length): 보통 거쳐진 Edge의 개수 (Vertex 개수 -1)
      --> 같은 Vertex를 두 번 지나지 않는 경로를 Simple Path로 부르기도 함

    -> Cycle(사이클): 시작 Vertex와 끝 Vertex가 같은 Path로 한 바퀴 돌아서 제자리로 오는 경로
      --> ex) A - B - C - A
      --> 사이클이 없는 그래프 = 비순환 그래프(Acyclic Graph)
      --> 방향 그래프에서 사이클이 없는 것 = DAG(Directed Acyclic Graph) - 빌드 의존성, 작업 스케줄링(위상 정렬) 등의 핵심 전제 조건
      --> 트리가 그래프의 특수 형태인 이유 중 하나가 트리는 사이클이 없다는 것, 사이클이 있다면 트리가 아님

    -> Connected(연결)
      --> Connected (두 정점 사이): 두 Vertex 사이에 Path가 하나라도 존재 한다면 Connected
      --> Connected Graph (그래프 전체): 모든 Vertex 쌍이 서로 Connected한 그래프 -> 그래프 전체가 하나로 이어져 있음
      --> 그래프가 여러 조각으로 나뉘어 있으면 각 조각을 Conneceted Component(연결 요소)라고 부름
        ---> ex) SNS 그래프에서 친구 관계로 전혀 안 이어진 별개의 그룹 이 여러개 있으면 각각이 Connected Component
      --> 방향 그래프에는 이 개념이 두 가지로 더 세분화
        ---> Weakly Connected: 방향을 무시했을 때 연결
        ---> Strongly Connected: 방향을 그대로 뒀을때도 양방향으로 서로 도달 가능

    -> Tree가 사이클이 없는 이유: root 제외 모든 노드는 부모가 하나 뿐 이라 Path가 유일하기 때문
    --> Connected & Acyclic
*/

public class _8_1_Graph_Basic {}
