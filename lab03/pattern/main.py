import pygame
import random
from abc import ABC, abstractmethod


WIDTH, HEIGHT = 1100, 700
FPS = 60
CARD_W, CARD_H = 150, 220

CLR = {
    'bg': (20, 22, 28), 'grid': (30, 33, 42), 'card': (43, 48, 62),
    'fire': (250, 80, 50), 'heal': (50, 230, 110), 'mana': (40, 180, 255),
    'hp': (220, 40, 60), 'dark': (140, 50, 220), 'phys': (200, 200, 200),
    'armor': (150, 160, 180)
}

pygame.init()
SCREEN = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Card Game: Template Method Showcase")
CLOCK = pygame.time.Clock()


def get_font(size, bold=False):
    return pygame.font.SysFont("Verdana", size, bold=bold)


FONT_L = get_font(48, True)
FONT_M = get_font(20, True)
FONT_S = get_font(13, True)



def draw_icon(surf, icon_type, color, cx, cy):
    if icon_type == "SWORD":
        pygame.draw.polygon(surf, color, [(cx, cy - 25), (cx - 5, cy + 10), (cx + 5, cy + 10)])
        pygame.draw.rect(surf, color, (cx - 12, cy + 10, 24, 4))
        pygame.draw.rect(surf, color, (cx - 3, cy + 14, 6, 10))
    elif icon_type == "FIRE":
        pygame.draw.circle(surf, color, (cx, cy + 5), 12)
        pygame.draw.polygon(surf, color, [(cx - 12, cy + 5), (cx + 12, cy + 5), (cx, cy - 20)])
    elif icon_type == "CROSS":
        pygame.draw.rect(surf, color, (cx - 6, cy - 20, 12, 40))
        pygame.draw.rect(surf, color, (cx - 20, cy - 6, 40, 12))
    elif icon_type == "LIGHTNING":
        pygame.draw.polygon(surf, color,
                            [(cx + 5, cy - 20), (cx - 10, cy), (cx + 2, cy), (cx - 8, cy + 20), (cx + 12, cy - 2),
                             (cx, cy - 2)])
    elif icon_type == "SHIELD":
        pygame.draw.polygon(surf, color, [(cx - 15, cy - 15), (cx + 15, cy - 15), (cx + 15, cy + 5), (cx, cy + 20),
                                          (cx - 15, cy + 5)])
    elif icon_type == "DROP":
        pygame.draw.circle(surf, color, (cx, cy + 5), 12)
        pygame.draw.polygon(surf, color, [(cx - 12, cy + 5), (cx + 12, cy + 5), (cx, cy - 15)])


# Паттерн

class Card(ABC):
    def __init__(self, name, cost, color, desc):
        self.name, self.cost, self.color, self.desc = name, cost, color, desc
        self.pos = pygame.Vector2(WIDTH // 2, HEIGHT + 300)  # Карты плавно выезжают снизу
        self.target_pos = pygame.Vector2(0, 0)
        self.angle, self.target_angle = 0, 0
        self.scale = 1.0
        self.is_hovered = False

    def play_card(self, owner, opponent, effects) -> bool:
        if owner.mana < self.cost:
            effects.append(FloatingText(owner.pos, "НЕТ МАНЫ!", CLR['mana']))
            return False
        owner.mana -= self.cost
        power = self._get_power()
        self._apply_effect(owner, opponent, power, effects)
        return True

    @abstractmethod
    def _get_power(self) -> int: pass

    @abstractmethod
    def _apply_effect(self, owner, opponent, power, effects): pass

    @abstractmethod
    def _get_icon_type(self) -> str: pass



class StrikeCard(Card):
    def __init__(self): super().__init__("УДАР", 1, CLR['phys'], "Урон: 8")

    def _get_power(self): return 8

    def _get_icon_type(self): return "SWORD"

    def _apply_effect(self, owner, opponent, power, effects):
        effects.append(Projectile(owner.pos, opponent.pos, self.color, lambda: opponent.take_damage(power)))


class ShieldCard(Card):
    def __init__(self): super().__init__("СТАЛЬНОЙ ПАНЦИРЬ", 1, CLR['armor'], "Броня: 10\nСпадает в начале хода")

    def _get_power(self): return 10

    def _get_icon_type(self): return "SHIELD"

    def _apply_effect(self, owner, opponent, power, effects):
        owner.armor += power
        effects.append(FloatingText(owner.pos, f"+{power} БРОНЯ", self.color))
        for _ in range(15): effects.append(Particle(owner.pos, self.color))


class MeteorCard(Card):
    def __init__(self): super().__init__("МЕТЕОРИТ", 4, CLR['fire'], "Сокрушительный\nурон: 25")

    def _get_power(self): return 25

    def _get_icon_type(self): return "FIRE"

    def _apply_effect(self, owner, opponent, power, effects):
        effects.append(Projectile(owner.pos, opponent.pos, self.color, lambda: opponent.take_damage(power), size=16))


class HealCard(Card):
    def __init__(self): super().__init__("ИСТОЧНИК", 2, CLR['heal'], "Хил: 12")

    def _get_power(self): return 12

    def _get_icon_type(self): return "CROSS"

    def _apply_effect(self, owner, opponent, power, effects):
        owner.take_heal(power)
        effects.append(FloatingText(owner.pos, f"+{power} HP", self.color))
        for _ in range(25): effects.append(Particle(owner.pos, self.color))


class FocusCard(Card):
    def __init__(self): super().__init__("МЕДИТАЦИЯ", 0, CLR['mana'], "Мана: +3")

    def _get_power(self): return 3

    def _get_icon_type(self): return "LIGHTNING"

    def _apply_effect(self, owner, opponent, power, effects):
        owner.mana = min(owner.mana + power, 10)
        effects.append(FloatingText(owner.pos, f"+{power} МАНА", self.color))
        for _ in range(15): effects.append(Particle(owner.pos, self.color))


# Визуал

class Projectile:
    def __init__(self, start_pos, target_pos, color, on_hit, size=8):
        self.pos, self.target = pygame.Vector2(start_pos), pygame.Vector2(target_pos)
        self.color, self.on_hit, self.size = color, on_hit, size
        self.speed, self.life = 25, 1

    def update(self, effects):
        dir_vec = self.target - self.pos
        if dir_vec.length() < self.speed:
            self.on_hit()
            self.life = 0
            for _ in range(30): effects.append(Particle(self.target, self.color, size=self.size))
        else:
            self.pos += dir_vec.normalize() * self.speed
            effects.append(Particle(self.pos, self.color, size=self.size - 2, life=100))

    def draw(self, surf):
        pygame.draw.circle(surf, self.color, (int(self.pos.x), int(self.pos.y)), self.size)


class Particle:
    def __init__(self, pos, color, size=6, life=255):
        self.pos, self.color = pygame.Vector2(pos), color
        self.size, self.life = size, life
        self.vel = pygame.Vector2(random.uniform(-4, 4), random.uniform(-4, 4))

    def update(self, effects):
        self.pos += self.vel;
        self.life -= 15

    def draw(self, surf):
        if self.life > 0:
            s = pygame.Surface((self.size * 2, self.size * 2), pygame.SRCALPHA)
            pygame.draw.circle(s, (*self.color, self.life), (self.size, self.size), self.size)
            surf.blit(s, (self.pos.x - self.size, self.pos.y - self.size), special_flags=pygame.BLEND_RGBA_ADD)


class FloatingText:
    def __init__(self, pos, text, color):
        self.pos = pygame.Vector2(pos) + pygame.Vector2(random.randint(-20, 20), -50)
        self.text, self.color, self.life = text, color, 255

    def update(self, effects): self.pos.y -= 1.5; self.life -= 4

    def draw(self, surf):
        if self.life > 0:
            txt = FONT_M.render(self.text, True, self.color)
            txt.set_alpha(max(0, self.life))
            surf.blit(txt, self.pos)



# Игроки

class Entity:
    def __init__(self, name, pos, color, hp=80):
        self.name, self.pos, self.color = name, pygame.Vector2(pos), color
        self.hp, self.max_hp, self.mana = hp, hp, 3
        self.armor = 0
        self.shake = 0

    def take_damage(self, val):
        self.shake = 25
        if self.armor > 0:
            if val <= self.armor:
                self.armor -= val
                return
            else:
                val -= self.armor
                self.armor = 0
        self.hp -= val

    def take_heal(self, val):
        self.hp = min(self.hp + val, self.max_hp)

    def draw(self, surf):
        off_x = random.randint(-self.shake, self.shake) if self.shake > 0 else 0
        off_y = random.randint(-self.shake, self.shake) if self.shake > 0 else 0
        if self.shake > 0: self.shake -= 1

        r = pygame.Rect(self.pos.x - 110 + off_x, self.pos.y + 40 + off_y, 220, 24)
        pygame.draw.rect(surf, (30, 30, 35), r, border_radius=10)

        fill_w = max(0, min(1, self.hp / self.max_hp)) * 220
        if fill_w > 0:
            pygame.draw.rect(surf, CLR['hp'], (r.x, r.y, fill_w, 24), border_radius=10)

        surf.blit(FONT_M.render(self.name, True, self.color), (r.x, r.y - 38))

        hp_text = FONT_S.render(f"{max(0, int(self.hp))} / {self.max_hp}", True, (255, 255, 255))
        surf.blit(hp_text, (r.x + 110 - hp_text.get_width() // 2, r.y + 3))

        if self.armor > 0:
            shield_color = CLR['armor']
            pygame.draw.polygon(surf, shield_color,
                                [(r.x - 20, r.y), (r.x + 5, r.y), (r.x + 5, r.y + 15), (r.x - 8, r.y + 25),
                                 (r.x - 20, r.y + 15)])
            pygame.draw.polygon(surf, (200, 210, 230),
                                [(r.x - 20, r.y), (r.x + 5, r.y), (r.x + 5, r.y + 15), (r.x - 8, r.y + 25),
                                 (r.x - 20, r.y + 15)], width=2)
            ar_text = FONT_S.render(str(self.armor), True, (20, 20, 20))
            surf.blit(ar_text, (r.x - 8 - ar_text.get_width() // 2, r.y + 4))

        mana_y = r.y + 35
        surf.blit(FONT_S.render("МАНА:", True, CLR['mana']), (r.x, mana_y))
        for m in range(10):
            cx = r.x + 60 + m * 15
            cy = mana_y + 8
            if m < self.mana:
                pygame.draw.circle(surf, CLR['mana'], (cx, cy), 5)
                pygame.draw.circle(surf, (255, 255, 255), (cx, cy), 5, width=1)
            else:
                pygame.draw.circle(surf, (40, 40, 50), (cx, cy), 5)



class Engine:
    def __init__(self):
        self.state = "MENU"
        self.player_hand, self.enemy_hand, self.effects = [], [], []
        self.pool = [StrikeCard, StrikeCard, ShieldCard, HealCard, FocusCard, MeteorCard]
        self.banner = {"text": "", "timer": 0, "color": (0, 0, 0)}

    def init_game(self, difficulty):
        self.player = Entity("МАГ (ИГРОК)", (250, 250), (100, 220, 255), hp=80)
        boss_hps = {"ЛЁГКАЯ": 60, "НОРМАЛЬНАЯ": 120, "БЕЗУМНАЯ": 220}
        self.enemy = Entity("ТЁМНЫЙ ЛОРД", (850, 250), CLR['fire'], hp=boss_hps[difficulty])

        self.player_hand.clear()
        self.enemy_hand.clear()
        self.effects.clear()
        self.start_player_turn()
        self.state = "PLAYING"

    def trigger_banner(self, text, color):
        self.banner = {"text": text, "timer": 90, "color": color}

    def start_player_turn(self):
        self.turn = "PLAYER"
        self.player.armor = 0
        self.player.mana = min(self.player.mana + 3, 10)
        self.trigger_banner("ВАШ ХОД", CLR['mana'])
        while len(self.player_hand) < 5:
            self.player_hand.append(random.choice(self.pool)())

    def start_enemy_turn(self):
        self.turn = "ENEMY"
        self.enemy.armor = 0
        self.enemy.mana = min(self.enemy.mana + 3, 10)
        self.trigger_banner("ХОД ВРАГА", CLR['hp'])
        self.ai_timer = 90
        while len(self.enemy_hand) < 5:
            self.enemy_hand.append(random.choice(self.pool)())

    def update_playing(self):
        if self.player.hp <= 0:
            self.state, self.win = "GAME_OVER", False
        elif self.enemy.hp <= 0:
            self.state, self.win = "GAME_OVER", True

        if self.turn == "ENEMY":
            self.ai_timer -= 1
            if self.ai_timer <= 0:
                affordable = [c for c in self.enemy_hand if c.cost <= self.enemy.mana]
                if not affordable or (random.random() < 0.15 and self.enemy.mana < 10):
                    self.start_player_turn()
                else:
                    card = random.choice(affordable)
                    self.enemy_hand.remove(card)
                    card.play_card(self.enemy, self.player, self.effects)
                    self.ai_timer = 80

        for e in self.effects[:]:
            e.update(self.effects)
            if getattr(e, 'life', 0) <= 0: self.effects.remove(e)

        mx, my = pygame.mouse.get_pos()
        num_cards = len(self.player_hand)
        for i, card in enumerate(self.player_hand):
            offset = i - (num_cards - 1) / 2
            card.target_angle = -offset * 6
            card.target_pos = pygame.Vector2(WIDTH // 2 + offset * 110, HEIGHT - 110 + abs(offset) * 12)

            rect = pygame.Rect(card.pos.x - CARD_W // 2, card.pos.y - CARD_H // 2, CARD_W, CARD_H)

            if rect.collidepoint(mx, my) and self.turn == "PLAYER" and self.banner["timer"] <= 0:
                card.target_pos.y -= 100
                card.target_angle = 0
                card.scale += (1.15 - card.scale) * 0.2
                card.is_hovered = True
            else:
                card.scale += (1.0 - card.scale) * 0.2
                card.is_hovered = False

            card.pos += (card.target_pos - card.pos) * 0.15
            card.angle += (card.target_angle - card.angle) * 0.15

    def draw_bg(self):
        SCREEN.fill(CLR['bg'])
        for x in range(0, WIDTH, 100): pygame.draw.line(SCREEN, CLR['grid'], (x, 0), (x, HEIGHT))
        for y in range(0, HEIGHT, 100): pygame.draw.line(SCREEN, CLR['grid'], (0, y), (WIDTH, y))

    def draw_enemy_hand_ui(self):
        num_cards = len(self.enemy_hand)
        for i in range(num_cards):
            offset = i - (num_cards - 1) / 2
            x = self.enemy.pos.x + offset * 45
            y = self.enemy.pos.y - 130
            rect = pygame.Rect(x - 20, y - 30, 40, 60)
            pygame.draw.rect(SCREEN, CLR['card'], rect, border_radius=6)
            pygame.draw.rect(SCREEN, (60, 65, 80), rect, width=2, border_radius=6)
            pygame.draw.circle(SCREEN, CLR['fire'], (int(x), int(y)), 8, width=2)

    def draw_card(self, card):
        surf = pygame.Surface((CARD_W, CARD_H), pygame.SRCALPHA)
        border_color = CLR['heal'] if (card.is_hovered and self.player.mana >= card.cost) else (80, 85, 100)

        pygame.draw.rect(surf, CLR['card'], (0, 0, CARD_W, CARD_H), border_radius=12)
        pygame.draw.rect(surf, border_color, (0, 0, CARD_W, CARD_H), width=4, border_radius=12)

        surf.blit(FONT_S.render(card.name, True, card.color), (15, 15))

        pygame.draw.circle(surf, (25, 28, 35), (CARD_W // 2, CARD_H // 2 - 15), 45)
        pygame.draw.circle(surf, card.color, (CARD_W // 2, CARD_H // 2 - 15), 45, width=2)
        draw_icon(surf, card._get_icon_type(), card.color, CARD_W // 2, CARD_H // 2 - 15)

        pygame.draw.circle(surf, CLR['mana'], (CARD_W - 25, 25), 18)
        pygame.draw.circle(surf, (255, 255, 255), (CARD_W - 25, 25), 18, width=2)
        surf.blit(FONT_M.render(str(card.cost), True, (20, 20, 20)), (CARD_W - 32, 11))

        lines = card.desc.split('\n')
        for idx, line in enumerate(lines):
            surf.blit(FONT_S.render(line, True, (180, 180, 180)), (15, 155 + idx * 18))

        if card.scale != 1.0:
            scaled_w, scaled_h = int(CARD_W * card.scale), int(CARD_H * card.scale)
            surf = pygame.transform.smoothscale(surf, (scaled_w, scaled_h))

        rotated = pygame.transform.rotate(surf, card.angle)
        rect = rotated.get_rect(center=(int(card.pos.x), int(card.pos.y)))
        SCREEN.blit(rotated, rect.topleft)

    def draw_banner(self):
        if self.banner["timer"] > 0:
            self.banner["timer"] -= 1
            alpha = min(200, self.banner["timer"] * 5)
            overlay = pygame.Surface((WIDTH, HEIGHT), pygame.SRCALPHA)
            overlay.fill((0, 0, 0, alpha // 2))
            band = pygame.Rect(0, HEIGHT // 2 - 50, WIDTH, 100)
            pygame.draw.rect(overlay, (*self.banner["color"], alpha // 1.5), band)
            SCREEN.blit(overlay, (0, 0))
            txt = FONT_L.render(self.banner["text"], True, (255, 255, 255))
            txt.set_alpha(alpha)
            SCREEN.blit(txt, (WIDTH // 2 - txt.get_width() // 2, HEIGHT // 2 - txt.get_height() // 2))

    def draw_turn_indicator(self):
        """Отрисовка кнопки/текста завершения хода сверху по центру"""
        text = "[ПРОБЕЛ] Завершить ход" if self.turn == "PLAYER" else "ХОД ВРАГА..."
        color = (255, 255, 255) if self.turn == "PLAYER" else CLR['fire']

        txt_surf = FONT_M.render(text, True, color)
        bg_rect = pygame.Rect(WIDTH // 2 - txt_surf.get_width() // 2 - 20, 15, txt_surf.get_width() + 40, 40)


        bg_surf = pygame.Surface((bg_rect.width, bg_rect.height), pygame.SRCALPHA)
        pygame.draw.rect(bg_surf, (30, 33, 42, 200), bg_surf.get_rect(), border_radius=10)
        if self.turn == "PLAYER":
            pygame.draw.rect(bg_surf, (80, 85, 100, 200), bg_surf.get_rect(), width=2, border_radius=10)

        SCREEN.blit(bg_surf, bg_rect.topleft)
        SCREEN.blit(txt_surf, (WIDTH // 2 - txt_surf.get_width() // 2, 23))

    def draw_game_over(self):
        self.draw_bg()

        if self.win:
            title_text = "ЛАБОРАТОРНАЯ СДАНА!"
            color = CLR['heal']
            story = [
                "СЕКРЕТ РАСКРЫТ! Под маской Тёмного Лорда всё это время",
                "скрывался Радмир Ренатович! Он не хотел просто так ставить",
                "оценку и пытался завалить студента каверзными вопросами.",
                "Но боевой дух юного программиста и идеальный",
                "Шаблонный Метод сломили сопротивление!",
                "Лабораторная работа блестяще защищена на ОТЛИЧНО!"
            ]
        else:
            title_text = "НЕОЖИДАННЫЙ ПОВОРОТ!"
            color = CLR['mana']
            story = [
                "Оказывается, Магом (игроком) всё это время был сам",
                "Радмир Ренатович, который пытался завалить Студента",
                "(Тёмного Лорда) на защите лабораторной работы!",
                "Но студент оказался слишком хорош: он играючи отбил",
                "все вопросы и блестяще защитил свой код!",
                "(Отличная попытка, Радмир Ренатович!)"
            ]

        # Заголовок
        title = FONT_L.render(title_text, True, color)
        SCREEN.blit(title, (WIDTH // 2 - title.get_width() // 2, 120))

        # Отрисовка многострочного текста
        start_y = 220
        for i, line in enumerate(story):
            txt = FONT_M.render(line, True, (220, 220, 230))
            SCREEN.blit(txt, (WIDTH // 2 - txt.get_width() // 2, start_y + i * 35))

        # Подсказка для выхода
        sub = FONT_M.render("Нажмите [ПРОБЕЛ] для возврата в меню", True, (150, 150, 150))
        SCREEN.blit(sub, (WIDTH // 2 - sub.get_width() // 2, 500))

    def run(self):
        while True:
            for event in pygame.event.get():
                if event.type == pygame.QUIT: return

                if self.state == "PLAYING":
                    if event.type == pygame.MOUSEBUTTONDOWN and self.turn == "PLAYER" and self.banner["timer"] <= 0:
                        mx, my = pygame.mouse.get_pos()
                        for card in reversed(self.player_hand):
                            w, h = CARD_W * card.scale, CARD_H * card.scale
                            rect = pygame.Rect(card.pos.x - w // 2, card.pos.y - h // 2, w, h)
                            if rect.collidepoint(mx, my):
                                if card.play_card(self.player, self.enemy, self.effects):
                                    self.player_hand.remove(card)
                                break

                    if event.type == pygame.KEYDOWN and event.key == pygame.K_SPACE and self.turn == "PLAYER":
                        self.start_enemy_turn()

                elif self.state == "GAME_OVER":
                    if event.type == pygame.KEYDOWN and event.key == pygame.K_SPACE:
                        self.state = "MENU"

            if self.state == "MENU":
                self.draw_bg()
                title = FONT_L.render("PATTERNS & STUDENTS", True, CLR['mana'])
                SCREEN.blit(title, (WIDTH // 2 - title.get_width() // 2, 120))

                btns = [("ЛЁГКАЯ", 300, CLR['heal']), ("НОРМАЛЬНАЯ", 380, (255, 200, 50)), ("БЕЗУМНАЯ", 460, CLR['hp'])]
                mx, my = pygame.mouse.get_pos()
                for text, y, color in btns:
                    rect = pygame.Rect(WIDTH // 2 - 120, y, 240, 60)
                    hover = rect.collidepoint(mx, my)
                    pygame.draw.rect(SCREEN, color if hover else (50, 50, 60), rect, border_radius=12)
                    pygame.draw.rect(SCREEN, (255, 255, 255), rect, width=2, border_radius=12)
                    txt = FONT_M.render(text, True, (20, 20, 25) if hover else (255, 255, 255))
                    SCREEN.blit(txt, (WIDTH // 2 - txt.get_width() // 2, y + 15))
                    if hover and pygame.mouse.get_pressed()[0]:
                        self.init_game(text)
                        pygame.time.delay(200)

            elif self.state == "GAME_OVER":
                self.draw_game_over()

            elif self.state == "PLAYING":
                self.draw_bg()
                self.update_playing()

                self.player.draw(SCREEN)
                self.enemy.draw(SCREEN)
                self.draw_enemy_hand_ui()
                self.draw_turn_indicator()

                for e in self.effects:
                    if not isinstance(e, Projectile) and not isinstance(e, Particle): e.draw(SCREEN)
                for e in self.effects:
                    if isinstance(e, Projectile) or isinstance(e, Particle): e.draw(SCREEN)

                for card in self.player_hand: self.draw_card(card)

                self.draw_banner()

            pygame.display.flip()
            CLOCK.tick(FPS)


if __name__ == "__main__":
    Engine().run()