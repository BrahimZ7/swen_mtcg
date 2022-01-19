# swen_mtcg

Link zu Github Projekt: https://github.com/BrahimZ7/swen_mtcg

Design:
Am Anfang war ich mir nicht ganz sicher wie ich die Klassen unterteilen möchte, ich hab es dann ganz einfach gemacht und zwar gibt es einmal die Handler, da befinden sich die State Management Klassen welche sich den State des Servers kümmern sowie aber auch die einkommenden Requests. Als nächstes gibt es dann noch den Model Ordner welcher einfach nur Daten Klassen um sich einiges beim Programmieren zu vereinfachen. Ein gutes Beispiel dafür ist die HTTPModel Klasse, durch diese Klasse musste ich das Parsen von den HTTP Headern extra machen und konnte auch so viel schneller und angenehmer auf die wichtigen Sachen zugreifen. Als letztes gibt es dann noch die Services, dort liegt nur die DatabaseService Klasse welche sich nur um die SQL Datenbank kümmert.

Lessons Learned:
    - ein gutes Software Architektur Model ist sehr wichtig
    - vorher planen dann programmieren
    - Zeit besser einplanen
    - generell vor dem Programmieren einen Plan machen wie das ganze Projekt umgesetzt werden soll

Unit Testing decisions:
Ich habe mehr den Fokus auf die State Management Klassen gelegt weil genau dort passiert die größte Logik vom ganzen Server. 

